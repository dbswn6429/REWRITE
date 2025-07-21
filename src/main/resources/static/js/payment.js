// 결제 금액 정보
let amount = {
    currency: "KRW",
    value: 50000, // 기본값 (실제로는 서버에서 받은 값으로 대체됨)
};

let selectedPaymentMethod = "CARD"; // 기본값은 신용카드
let orderItems = [];

// ------  SDK 초기화 ------
const clientKey = "test_ck_nRQoOaPz8L5b264AqJOz3y47BMw6";
const customerKey = generateRandomString();
const tossPayments = TossPayments(clientKey);
const payment = tossPayments.payment({
    customerKey,
});

// 페이지 로드 시 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM 로드 완료: 결제 초기화 시작");

    // 결제 버튼에 이벤트 리스너 추가
    const checkoutButton = document.querySelector('.checkout-button');
    if (checkoutButton) {
        checkoutButton.addEventListener('click', function() {
            console.log("결제 버튼 클릭됨");
            placeOrder();
        });
        console.log("결제 버튼 이벤트 리스너 설정 완료");
    } else {
        console.error("결제 버튼을 찾을 수 없습니다");
    }

    // 결제 방법 라디오 버튼에 이벤트 리스너 추가
    const radios = document.querySelectorAll('input[name="payment-method"]');
    radios.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.checked) {
                switch(this.value) {
                    case "credit-card":
                        selectPaymentMethod("CARD");
                        break;
                    case "TRANSFER":
                        selectPaymentMethod("TRANSFER");
                        break;
                    case "toss-pay":
                        selectPaymentMethod("TOSS_PAY");
                        break;
                }
            }
        });
    });

    updatePaymentAmount();
    collectOrderItems();
});

function selectPaymentMethod(method) {
    selectedPaymentMethod = method;
    console.log("결제 방법 선택됨:", method);
}

function collectOrderItems() {
    const productRows = document.querySelectorAll('.product-table tbody tr');
    orderItems = Array.from(productRows).map(row => {
        const productName = row.querySelector('.product-name')?.textContent || "상품명 없음";
        const productPrice = row.querySelector('.product-price')?.textContent.replace(/[^0-9]/g, '') || "0";
        const productImage = row.querySelector('.product-thumbnail')?.getAttribute('src') || "";
        return {
            name: productName,
            price: parseInt(productPrice, 10),
            image: productImage
        };
    });
    console.log("상품 정보 수집 완료:", orderItems.length + "개 상품");
}

function updatePaymentAmount() {
    const orderAmountElement = document.querySelector('.summary-row:nth-child(1) span:nth-child(2)');
    const shippingFeeElement = document.querySelector('.summary-row:nth-child(2) span:nth-child(2)');
    const totalAmountElement = document.querySelector('.total-amount');

    if (orderAmountElement && shippingFeeElement && totalAmountElement) {
        const orderAmountValue = parseInt(orderAmountElement.textContent.replace(/[^0-9]/g, ''), 10) || 0;
        const shippingFeeValue = parseInt(shippingFeeElement.textContent.replace(/[^0-9]/g, ''), 10) || 0;
        const totalAmountValue = parseInt(totalAmountElement.textContent.replace(/[^0-9]/g, ''), 10) || 0;

        amount.value = totalAmountValue || (orderAmountValue + shippingFeeValue);
        window.orderAmount = orderAmountValue;
        window.shippingFee = shippingFeeValue;

        console.log("결제 금액 업데이트:", amount.value + "원");
    } else {
        console.error("결제 금액 요소를 찾을 수 없습니다");
    }
}

async function placeOrder() {
    console.log("결제 프로세스 시작");
    updatePaymentAmount();

    // 결제 금액 유효성 검사
    if (!amount.value || amount.value <= 0) {
        alert("결제 금액이 유효하지 않습니다. 페이지를 새로고침 후 다시 시도해주세요.");
        console.error("유효하지 않은 결제 금액:", amount.value);
        return;
    }

    try {
        const orderId = generateRandomString();
        // 올바른 orderName 생성
        let orderName = orderItems.length > 0 ?
            orderItems[0].name + (orderItems.length > 1 ? ` 외 ${orderItems.length - 1}건` : '') :
            "상품 주문";

        const receiverName = document.querySelector('.info-row:nth-child(1) .info-value')?.textContent.trim() || "수령인 정보 없음";
        const receiverPhone = document.querySelector('.info-row:nth-child(2) .info-value')?.textContent.trim() || "";
        const addressInfo = document.querySelector('.info-row:nth-child(3) .info-value span')?.textContent.trim() || "";
        const deliveryRequest = document.querySelector('.info-value.input-value input')?.value.trim() || "";

        // 우편번호 올바르게 추출
        const postcodeMatch = addressInfo.match(/\(([^)]+)\)/);
        const postcode = postcodeMatch ? postcodeMatch[1] : '';
        const addressWithoutPostcode = addressInfo.replace(/\s*\([^)]+\)/, '').trim();
        const addrParts = addressWithoutPostcode.split(' ');
        const detailAddr = addrParts.pop() || '';
        const addr = addrParts.join(' ');

        // Orders 엔티티에 맞게 데이터 구성
        const orderData = {
            // 주문 정보
            orderId: orderId,
            totalPrice: window.orderAmount || 0,
            shippingFee: window.shippingFee || 0,
            finalPrice: amount.value,
            // 배송지 정보
            receiverName: receiverName,
            receiverPhone: receiverPhone,
            postcode: postcode,
            addr: addr,
            detailAddr: detailAddr,
            deliveryRequest:deliveryRequest,
            // 주문 시각은 서버에서 처리
            orderedAt: new Date().toISOString(),
            // 상품 정보
            orderItems: orderItems,
            paymentMethod: selectedPaymentMethod
        };

        console.log("주문 데이터 생성:", orderId);
        sessionStorage.setItem('orderData', JSON.stringify(orderData));

        const commonParams = {
            amount: amount,
            orderId: orderId,
            orderName: orderName,
            successUrl: window.location.origin + "/prod/orderSuccess?deliveryRequest=" + encodeURIComponent(deliveryRequest),
            failUrl: window.location.origin + "/prod/orderPay",
            customerEmail: "", // 사용자 이메일 정보가 있으면 추가
            customerName: receiverName
        };

        console.log("결제 요청 준비:", selectedPaymentMethod);

        switch (selectedPaymentMethod) {
            case "CARD":
                console.log("카드 결제 요청 시작");
                await payment.requestPayment({
                    method: "CARD",
                    ...commonParams,
                    card: {
                        useEscrow: false,
                        flowMode: "DEFAULT",
                        useCardPoint: false,
                        useAppCardOnly: false,
                    },
                });
                break;
            case "TRANSFER":
                console.log("계좌이체 결제 시작");
                await payment.requestPayment({
                    method: "TRANSFER",
                    ...commonParams
                });
                break;
            case "TOSS_PAY":
                console.log("토스페이 결제 요청 시작");
                await payment.requestPayment({
                    method: "EASY_PAY",
                    ...commonParams,
                    easyPay: "TOSSPAY",
                });
                break;
            default:
                alert("결제 방법을 선택해주세요.");
                console.error("선택된 결제 방법 없음");
                break;
        }
    } catch (error) {
        console.error("결제 요청 중 오류가 발생했습니다:", error);
        alert("결제 요청 중 오류가 발생했습니다: " + error.message);
    }
}

function generateRandomString() {
    return window.btoa(Math.random()).slice(0, 20);
}


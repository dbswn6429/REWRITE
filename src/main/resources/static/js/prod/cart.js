document.addEventListener('DOMContentLoaded', function() {
    // --- 요소 가져오기 ---
    const selectAllCheckbox = document.getElementById('select-all');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');
    const deleteButton = document.querySelector('.delete-selected-button'); // 이름은 그대로 유지
    const orderButton = document.querySelector('.order-button');
    const cartItemsContainer = document.querySelector('.cart-items');

    // --- 함수 정의 ---

    // 가격 문자열에서 숫자만 추출하는 함수 (예: "1,000,000원" -> 1000000)
    function parsePrice(priceString) {
        const numericString = priceString.replace(/[^0-9]/g, '');
        return parseInt(numericString, 10) || 0;
    }

    // 숫자를 통화 형식 문자열로 변환하는 함수 (예: 1000000 -> "1,000,000")
    function formatPrice(priceNumber) {
        return priceNumber.toLocaleString('ko-KR');
    }

    // 총 주문 금액 및 버튼 텍스트 업데이트 함수
    function updateTotalPrice() {
        let totalPrice = 0;
        const checkedItems = document.querySelectorAll('.item-checkbox:checked');

        // 선택된 상품이 없으면 0원으로 표시
        if (checkedItems.length === 0) {
            orderButton.textContent = "0원 주문하기";
            return;
        }

        checkedItems.forEach(checkbox => {
            const cartItem = checkbox.closest('.cart-item');
            if (cartItem) {
                const priceElement = cartItem.querySelector('.item-price');
                if (priceElement) {
                    const price = parsePrice(priceElement.textContent);
                    totalPrice += price;
                }
            }
        });

        // 주문 버튼 텍스트 업데이트
        orderButton.textContent = `${formatPrice(totalPrice)}원 주문하기`;
    }

    // '전체 선택' 체크박스 상태 업데이트 함수
    function updateSelectAllStatus() {
        const allItemCheckboxes = document.querySelectorAll('.item-checkbox');

        // 상품이 있는지 확인 후 모든 상품이 체크되었는지 확인
        const allChecked = allItemCheckboxes.length > 0 &&
            Array.from(allItemCheckboxes).every(cb => cb.checked);

        if (selectAllCheckbox) {
            selectAllCheckbox.checked = allChecked;
        }
    }

    // 장바구니가 비어있는지 확인하는 함수
    function checkEmptyCart() {
        const remainingItems = document.querySelectorAll('.cart-item');
        if (remainingItems.length === 0) {
            // 장바구니가 비었을 때 처리
            cartItemsContainer.innerHTML = '<div class="empty-cart">장바구니가 비어있습니다.</div>';

            // 버튼들 비활성화
            if (selectAllCheckbox) selectAllCheckbox.disabled = true;
            if (deleteButton) deleteButton.disabled = true;
        }
    }

    // 단일 상품 삭제 함수 (개별 상품 삭제에 사용)
    function deleteCartItem(cartId) {
        return fetch(`/api/cart/${cartId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('삭제 실패');
                }
                return response.text();
            });
    }

    // --- 이벤트 리스너 연결 ---

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function () {
            const isChecked = this.checked;
            itemCheckboxes.forEach(checkbox => {
                checkbox.checked = isChecked;

                // 체크 상태 서버로 전송
                const cartItem = checkbox.closest('.cart-item');
                const cartId = cartItem.dataset.cartId;

                fetch(`/api/cart/${cartId}/check`, {
                    method: 'PATCH',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ isChecked: isChecked })
                });
            });
            updateTotalPrice();
        });
    }

    // --- 2. 개별 체크박스 변경 시 ---
    itemCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            updateSelectAllStatus();
            updateTotalPrice();

            const cartItem = this.closest('.cart-item');
            const cartId = cartItem.dataset.cartId;
            const isChecked = this.checked;

            fetch(`/api/cart/${cartId}/check`, {
                method: 'PATCH',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({ isChecked: isChecked })
            });
        });
    });

    // --- 3. 전체 삭제 버튼 ---
    if (deleteButton) {
        deleteButton.addEventListener('click', function () {
            const cartItems = document.querySelectorAll('.cart-item');
            if (cartItems.length === 0) {
                alert('장바구니가 이미 비어있습니다.');
                return;
            }

            if (confirm('장바구니의 모든 상품을 삭제하시겠습니까?')) {
                const deletePromises = Array.from(cartItems).map(cartItem => {
                    const cartId = cartItem.dataset.cartId;
                    return deleteCartItem(cartId);
                });

                Promise.all(deletePromises)
                    .then(() => {
                        alert('장바구니의 모든 상품이 삭제되었습니다.');
                        window.location.reload();
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('삭제 중 오류가 발생했습니다.');
                    });
            }
        });
    }

    // --- 4. 개별 삭제 버튼 ---
    if (cartItemsContainer) {
        cartItemsContainer.addEventListener('click', function (event) {
            if (event.target.classList.contains('item-delete-button')) {
                event.preventDefault();
                const cartId = event.target.dataset.cartId;

                if (!cartId) {
                    console.error('상품 ID를 찾을 수 없습니다.');
                    return;
                }

                if (confirm('이 상품을 장바구니에서 삭제하시겠습니까?')) {
                    deleteCartItem(cartId)
                        .then(() => {
                            alert('상품이 장바구니에서 삭제되었습니다.');
                            window.location.reload();
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('삭제 중 오류가 발생했습니다.');
                        });
                }
            }
        });
    }

    // --- 5. 주문하기 버튼 ---
    if (orderButton) {
        orderButton.addEventListener('click', function () {
            const cartItems = document.querySelectorAll('.cart-item');
            if (cartItems.length === 0) {
                alert('장바구니가 비어있습니다. 상품을 추가해주세요.');
                return;
            }

            const checkedItems = document.querySelectorAll('.item-checkbox:checked');
            if (checkedItems.length === 0) {
                alert('주문할 상품을 선택해주세요.');
                return;
            }

            const selectedCartIds = Array.from(checkedItems).map(checkbox => {
                return checkbox.closest('.cart-item').dataset.cartId;
            });

            const queryString = selectedCartIds.map(id => `cartId=${id}`).join('&');
            location.href = `/prod/orderPay?${queryString}`;
        });
    }

    // --- 초기 상태 설정 ---
    updateTotalPrice();
    checkEmptyCart();
});
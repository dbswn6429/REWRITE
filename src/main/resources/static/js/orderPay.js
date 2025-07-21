// 전역 함수로 정의 - 팝업 닫기 함수
function closePay() {
    console.log("closePay 함수 호출됨");
    $('#payPop').hide();
    $('#payPop').attr('style', 'display: none !important');
    document.getElementById('payPop').style.display = 'none';
}

// 전역 함수로 정의 (document.ready 바깥으로 이동)
function openPay() {
    console.log("openPay 함수 호출됨");
    // AJAX 요청으로 배송지 목록 가져오기
    $.ajax({
        url: '/address/get-addresses',
        type: 'GET',
        success: function (addresses) {
            console.log("배송지 데이터 로드 성공:", addresses);
            // payPop 내부의 배송지 목록 컨테이너 초기화
            $('#payPop_section').empty();

            // 배송지 정보가 없는 경우
            if (addresses.length === 0) {
                $('#payPop_section').append('<p>등록된 배송지가 없습니다.</p>');
            } else {
                // 각 배송지 정보를 카드로 표시
                addresses.forEach(function (address) {
                    var addressParts = address.address.split('/');
                    var phoneNum = address.phoneNum;
                    var formattedPhone = phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 7) + "-" + phoneNum.substring(7);
                    var addressFull = addressParts[1] + ' ' + addressParts[2] + ' (' + addressParts[0] + ')';

                    var card = `
                    <div class="address-box">
                        <div class="address-info">
                            <span>${address.addressAlias || '배송지'}</span>
                            <span class="default">${address.isDefault === 'C' ? '기본주소지' : ''}</span><br>
                            <span>${addressParts[1]}</span><br>
                            <span>${addressParts[2]}</span><br>
                            <span>(${addressParts[0]})</span><br>
                            <span>${formattedPhone}</span>
                        </div>
                       <div class="button-group" style="position: relative;">
                            <button type="button" class="btn btn-select" 
                                data-name="${address.name || '수령인'}" 
                                data-phone="${formattedPhone}" 
                                data-address="${addressFull}" 
                                data-id="${address.addressId}">선택</button>
                       </div>
                    </div>`;

                    $('#payPop_section').append(card);
                });
            }

            // 팝업 표시
            $('#payPop').show();
        }, error: function (error) {
            console.error('배송지 정보를 가져오는 중 오류 발생:', error);
        }
    });
}

// 부분 새로고침 함수
function refreshAddressSection() {
    // 배송지 정보 컨테이너만 새로고침
    $('.info-section').load(window.location.href + ' .info-section > *');
}

// 전역 함수로 정의 - 배송지 선택 함수
function selectDeliveryAddress(name, phone, addressFull, addressId) {
    console.log("배송지 선택됨:", name, phone, addressFull, addressId);

    // UI 업데이트
    $('.info-value.receiver-name').text(name);
    $('.info-value.receiver-phone').text(phone);
    $('.info-value.receiver-address').text(addressFull);
    $('input[name="addressId"]').val(addressId);

    // DB의 기본 배송지 업데이트 (is_default를 N에서 C로 변경)
    $.ajax({
        url: '/address/default',
        type: 'POST',
        data: {addressId: addressId},
        success: function(response) {
            console.log('기본 배송지가 DB에 업데이트되었습니다.');

            // // 배송지 정보 컨테이너만 새로고침
            // $('.info-section').load(window.location.href + ' .info-section > *');
            //
            // // 성공 후 UI 강제 업데이트 (부분 새로고침)
            // refreshAddressSection();

            // 성공 후 페이지 새로고침
            // window.location.reload();
        },
        error: function(error) {
            console.error('기본 배송지 업데이트 중 오류 발생:', error);
        },
        complete: function() {
            // 요청 완료 후 모달 닫기 (비동기 작업 완료 후 닫기 위함)
            setTimeout(closePay, 100);
        }
    });

    // 팝업 닫기
    closePay();
}

// // 배송지 섹션만 새로고침하는 함수
// function refreshAddressSection() {
//     $.ajax({
//         url: '/address/get-current', // 현재 배송지 정보만 가져오는 새 엔드포인트
//         type: 'GET',
//         success: function(data) {
//             // 가져온 데이터로 UI 업데이트
//             $('.address-section').html(data);
//         }
//     });
// }


// 문서 준비 완료 시 실행
$(document).ready(function () {
    console.log("문서 로드 완료");

    // 새 창으로 주소 팝업 여는 함수
    function openAddressPopup() {
        const popup = window.open('/user/address-popup', '배송지 변경', 'width=600,height=600,scrollbars=yes');
    }

    // 새 창에서 주소 선택 처리 함수
    function selectAddress(name, phone, addressFull, addressId) {
        if (window.opener) {
            window.opener.document.querySelector('.info-value.receiver-name').innerText = name;
            window.opener.document.querySelector('.info-value.receiver-phone').innerText = phone;
            window.opener.document.querySelector('.info-value.receiver-address').innerText = addressFull;
            window.opener.document.querySelector('input[name="addressId"]').value = addressId;

            window.close();
        }
        // window.location.reload();
    }

    // 배송지 변경 버튼 클릭 이벤트
    $(document).on("click", ".address-change-btn", function () {
        console.log("배송지 변경 버튼 클릭됨");
        openPay();
    });

    // 닫기 버튼 클릭 이벤트
    $(document).on("click", "#closePay", function () {
        console.log("닫기 버튼 클릭됨");
        closePay();
    });

    // 선택 버튼 클릭 이벤트
    $(document).on('click', '.btn-select', function () {
        // 클릭 즉시 버튼 비활성화
        $(this).prop('disabled', true);

        // 이후 배송지 정보 처리 코드
        // attr() 메서드로 데이터 가져오기
        const name = $(this).attr('data-name');
        const phone = $(this).attr('data-phone');
        const addressFull = $(this).attr('data-address');
        const addressId = $(this).attr('data-id');

        console.log("선택된 배송지:", name, phone, addressFull, addressId);

        // 데이터 업데이트
        $('.info-value.receiver-name').text(name);
        $('.info-value.receiver-phone').text(phone);
        $('.info-value.receiver-address').text(addressFull);
        $('input[name="addressId"]').val(addressId);

        // DB 업데이트 (기존 코드 사용)
        $.ajax({
            url: '/address/default',
            type: 'POST',
            data: {addressId: addressId},
            success: function (response) {
                console.log('기본 배송지 업데이트 성공');
                window.location.reload(); //개쩌는 실시간? 새로고침
            },
            error: function (error) {
                console.error('기본 배송지 업데이트 실패:', error);
            }
        });

        // 팝업 닫기
        closePay();
    });

    // 주소지 관련 기존 이벤트 처리
    if ($('.address-list').children().length === 0) {
        $('.address-list').append('<div class="no-address" style="margin: 30px 200px;">등록된 주소지가 없습니다.</div>');
    }

    $('.btn-edit').click(function () {
        var form = $(this).closest('form');
        form.attr('action', '/address/edit');
        form.submit();
    });

    $('.btn-delete').click(function () {
        var form = $(this).closest('form');
        if (form.parent().prev().find('.default').text() === '기본주소지') {
            alert("기본 주소지는 삭제할 수 없습니다.");
            return;
        }
        if (confirm("정말 삭제 하시겠습니까?")) {
            form.attr('action', '/address/delete');
            form.submit();
        }
    });

    $('.btn-add').click(function () {
        window.location.href = '/address/reg';
    });
});

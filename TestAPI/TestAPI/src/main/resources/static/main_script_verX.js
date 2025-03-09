document.addEventListener('DOMContentLoaded', function() {
    const imageGrid = document.getElementById('image-grid');
    const popup = document.getElementById('popup');
    const popupImage = document.getElementById('popup-image');
    const popupDescription = document.getElementById('popup-description');
    const closeButton = document.getElementById('close-button');
    const logo = document.getElementById('logo');
    const loginButton = document.getElementById('login-button');

    // 서버에서 이미지 목록 가져오기
    fetch('/images')
        .then(response => response.json())
        .then(data => {
            data.slice(0, 50).forEach(image => {
                const img = document.createElement('img');
                img.src = `/images/${image.fileName}`;
                img.alt = image.fileName;
                img.onerror = function() {
                    this.src = '/images/an.png'; // an.png로 대체
                };
                imageGrid.appendChild(img);

                // 이미지 클릭 시 팝업 열기 및 프롬프트 설정
                img.addEventListener('click', function() {
                    popup.style.display = 'flex';
                    popupImage.src = img.src;
                    popupDescription.textContent = `프롬프트: ${image.prompt} `; // 프롬프트 텍스트 설정
                });
            });
        })
        .catch(error => console.error('Error fetching images:', error));

    // 팝업 닫기 버튼
    closeButton.addEventListener('click', function() {
        popup.style.display = 'none';
    });

    // 팝업의 빈 공간 클릭 시 팝업 닫기
    popup.addEventListener('click', function(event) {
        if (event.target === popup) {
            popup.style.display = 'none';
        }
    });

    // 로고 클릭 시 메인 페이지로 이동
    logo.addEventListener('click', function() {
        window.location.href = 'index.html';
    });

    // 로그인 버튼 클릭 시 로그인 페이지로 이동
    loginButton.addEventListener('click', function() {
        window.location.href = 'login.html';
    });

    // 이미지 만들기 버튼에 이벤트 리스너 추가
    document.querySelector('.create-button').addEventListener('click', function() {
        window.location.href = 'index.html'; // 이미지 생성 페이지로 이동
    });

    document.querySelector('.login-form').addEventListener('submit', function(event) {
        event.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        console.log(`Username: ${username}`);
        console.log(`Password: ${password}`);

        alert('Login submitted!');
    });
});

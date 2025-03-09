document.addEventListener('DOMContentLoaded', function() {
    const printerOptionsContainer = document.getElementById('printer-options');
    const modelPreviewContainer = document.getElementById('model-preview');
    const waitingContainer = document.createElement('div');
    waitingContainer.id = 'waiting-container';
    document.body.appendChild(waitingContainer);

    const printers = [
        { id: 'printer1', name: 'Cubicon 3DP-110F', description: '사용 필라멘트: PLA', imageUrl: 'print_images/cubicon110f.png' },
        { id: 'printer2', name: 'Ultimaker 2+ Connect', description: '사용 필라멘트: Ultimaker PLA', imageUrl: 'print_images/ultimaker.png' }
    ];

    printers.forEach(printer => {
        const printerOption = document.createElement('div');
        printerOption.className = 'printer-option';

        const printerImage = document.createElement('img');
        printerImage.src = printer.imageUrl;
        printerImage.alt = printer.name;
        printerImage.className = 'printer-image';

        const printerList = document.createElement('div');
        printerList.className = 'printer-list';

        const label = document.createElement('label');
        label.textContent = printer.name;

        const radioButton = document.createElement('input');
        radioButton.type = 'radio';
        radioButton.name = 'printer_option';
        radioButton.value = printer.id;

        label.insertBefore(radioButton, label.firstChild);
        printerList.appendChild(label);

        const description = document.createElement('p');
        description.textContent = printer.description;
        printerList.appendChild(description);

        printerOption.appendChild(printerImage);
        printerOption.appendChild(printerList);

        printerOptionsContainer.appendChild(printerOption);
    });

    const urlParams = new URLSearchParams(window.location.search);
    const imageUrl = urlParams.get('image');
    const objUrl = urlParams.get('obj');
    const prompt = urlParams.get('prompt');

    const modelPreviewTitle = document.createElement('h2');
    modelPreviewTitle.textContent = '3D 모델 미리보기';

    const squareImage = document.createElement('div');
    squareImage.className = 'square-image';

    const previewImage = document.createElement('img');
    previewImage.src = imageUrl ? decodeURIComponent(imageUrl) : 'an.png';
    previewImage.alt = '3D 모델 미리보기';

    squareImage.appendChild(previewImage);
    modelPreviewContainer.appendChild(modelPreviewTitle);
    modelPreviewContainer.appendChild(squareImage);

    const nextPageButton = document.createElement('button');
    nextPageButton.id = 'mp2_page';
    nextPageButton.className = 'mp2_page';
    nextPageButton.textContent = '선택';
    modelPreviewContainer.appendChild(nextPageButton);

    let userId = '';

    printerOptionsContainer.addEventListener('click', (event) => {
        if (event.target.name === 'printer_option') {
            const options = document.querySelectorAll('input[name="printer_option"]');
            options.forEach(option => {
                option.checked = false;
            });
            event.target.checked = true;
        }
    });

    nextPageButton.addEventListener('click', () => {
        const selectedOption = document.querySelector('input[name="printer_option"]:checked');
        if (selectedOption) {
            const selectedPrinter = printers.find(printer => printer.id === selectedOption.value);
            const printerInfo = encodeURIComponent(JSON.stringify(selectedPrinter));
            const modelImage = encodeURIComponent(imageUrl);
            const modelObj = encodeURIComponent(objUrl);
            const encodedPrompt = encodeURIComponent(prompt);

            // 파일 생성 중 화면 표시
            document.body.innerHTML = '<h2>파일 생성 중입니다. 잠시만 기다려주세요...</h2>';
            const waitingMessage = document.createElement('p');
            waitingMessage.id = 'waiting-message';
            document.body.appendChild(waitingMessage);

            // 서버에 파일 생성 요청
            fetch(`/download-obj?objUrl=${modelObj}`)
                .then(response => response.json())
                .then(data => {
                    if (data.fileName) {
                        userId = data.fileName.replace('.obj', ''); // userId 저장
                        fetch(`/download-img?imgUrl=${encodeURIComponent(imageUrl)}&objFileName=${encodeURIComponent(userId)}&prompt=${encodeURIComponent(prompt)}`);
                        // 슬라이싱 요청 보내기
                        return fetch(`/slice-and-download?objFileName=${encodeURIComponent(data.fileName)}`);
                    } else {
                        alert('OBJ 파일 다운로드에 실패했습니다.');
                        throw new Error('OBJ 파일 다운로드에 실패했습니다.');
                    }
                })
                .then(response => response.json())
                .then(sliceData => {
                    if (sliceData.hvsFileName) {
                        // 대기 시간 업데이트 함수 호출
                        updateWaitingTime(userId);
                        window.location.href = `mp3_page.html?printer=${printerInfo}&image=${modelImage}&hvs=${encodeURIComponent(sliceData.hvsFileName)}&prompt=${encodedPrompt}&userId=${encodeURIComponent(userId)}`;
                    } else {
                        alert('파일 생성에 실패했습니다.');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('파일 생성 중 오류가 발생했습니다.');
                });

            // 대기 시간 업데이트 함수 호출
            updateWaitingTime(userId);
        } else {
            alert('프린터를 선택해주세요.');
        }
    });

    // 대기 시간 업데이트 함수
    function updateWaitingTime(userId) {
        fetch(`/queue-status?userId=${userId}`)
            .then(response => response.json())
            .then(data => {
                const position = data.position;
                const waitingTime = (position + 1) * 60; // 각 사용자의 대기 시간 계산
                const waitingMessage = document.getElementById('waiting-message');
                waitingMessage.textContent = `잠시만 기다려주세요...\n대기시간: ${waitingTime}초`;
                setTimeout(() => updateWaitingTime(userId), 1000); // 1초마다 대기 시간 업데이트
            })
            .catch(error => {
                console.error('Error fetching queue status:', error);
                setTimeout(() => updateWaitingTime(userId), 1000); // 에러가 발생해도 1초마다 재시도
            });
    }
});

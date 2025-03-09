document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const printerInfo = JSON.parse(decodeURIComponent(urlParams.get('printer')));
    const imageUrl = decodeURIComponent(urlParams.get('image'));
    const hvsFileName = decodeURIComponent(urlParams.get('hvs'));
    const prompt = decodeURIComponent(urlParams.get('prompt'));  // 추가된 부분

    if (printerInfo) {
        const printerName = document.querySelector('.printer-name');
        printerName.textContent = printerInfo.name;
    }

    const previewImage = document.querySelector('.square-image img');
    previewImage.src = imageUrl || 'default_image.png';
    previewImage.alt = '3D 모델 미리보기';

    const promptElement = document.querySelector('.output-time');
    promptElement.textContent = prompt;

    const downloadButton = document.getElementById('mp3_page');
    downloadButton.addEventListener('click', () => {
        fetch(`/download-hvs?fileName=${encodeURIComponent(hvsFileName)}`, {
            method: 'GET'
        })
            .then(response => {
                if (response.ok) {
                    return response.blob();
                } else {
                    throw new Error('HVS 파일 다운로드에 실패했습니다.');
                }
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.style.display = 'none';
                a.href = url;
                a.download = hvsFileName;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
            })
            .catch(error => {
                console.error('Error:', error);
                alert('HVS 파일 다운로드 중 오류가 발생했습니다.');
            });
    });

    document.querySelector('.go-main').addEventListener('click', function() {
        window.location.href = 'main_page.html';
    });
});

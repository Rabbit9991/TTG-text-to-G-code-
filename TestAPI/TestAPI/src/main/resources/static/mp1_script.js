let resultId = null;
let fetchCount = 0;
let loadingMessageElement = null;
let progressMessageElement = null;

//기존에 1차 2차로 나눠진 이미지 생성과 결과 출력을 하나로 통합시켜서 만들었음
async function generatePromptAndFetchResult(prompt) {
    //모드랑 아트스타일을 일단 하나로 고정시켜둠
    const mode = "preview";
    const artStyle = "realistic";

    try {
        const response = await fetch(`/generate-3d-text?mode=${mode}&prompt=${encodeURIComponent(prompt)}&art_style=${artStyle}`);
        if (response.ok) {
            const data = await response.json();
            resultId = data.result;
            fetchCount = 0; // fetch 카운트 리셋

            // 로딩 메시지 추가
            // 로딩 메세지 Loading~
            addLoadingMessage();
            // 출력 진행도 의미
            addProgressMessage();
            // 2차 결과 가져오기const
            fetchResult();
        } else {
            addMessageToChat("오류: " + response.status, 'bot');
        }
    } catch (error) {
        addMessageToChat("오류 발생: " + error.message, 'bot');
    }
}

async function fetchResult() {
    if (!resultId) {
        addMessageToChat("Result ID가 없습니다. 먼저 프롬프트를 생성하세요.", 'bot');
        return;
    }

    const intervalId = setInterval(async () => {
        try {
            const response = await fetch(`/fetch-result?resultId=${resultId}`);
            //fetchCount 체크하려고 만들어둔 건데 추후 삭제 필수
            fetchCount++;
            if (response.ok) {
                const data = await response.json();
                //api중 progress라고 진행도를 볼 수 있는 값이 있어서 그걸 이용해서 진행도 체크함
                updateProgressMessage(data.progress); // progress 업데이트
                if (data.status === "SUCCEEDED" && data.progress === 100) {
                    clearInterval(intervalId); // SUCCEEDED 상태이며 progress가 100일 경우 종료
                    removeLoadingMessage(); // 로딩 메시지 제거
                    removeProgressMessage(); // progress 메시지 제거
                    displayResult(data); // 결과 표시
                } else if (data.status === "FAILED") {
                    clearInterval(intervalId); // FAILED 상태일 경우 종료
                    removeLoadingMessage(); // 로딩 메시지 제거
                    removeProgressMessage(); // progress 메시지 제거
                    addMessageToChat("2차 결과 가져오기 실패: " + JSON.stringify(data, null, 2), 'bot');
                }
            } else {
                addMessageToChat("오류: " + response.status, 'bot');
            }
        } catch (error) {
            addMessageToChat("오류 발생: " + error.message, 'bot');
        }
    }, 5000); // 5초마다 요청
}

function displayResult(data) {
    if (data.thumbnail_url && data.model_urls && data.model_urls.obj && data.prompt) {
        addThumbnailToChat(data.thumbnail_url, data.model_urls.obj, data.prompt );
    } else {
        addMessageToChat("2차 결과: " + JSON.stringify(data, null, 2), 'bot');
    }
}

function addThumbnailToChat(thumbnailUrl, objUrl, UserPrompt) {
    const chatBox = document.getElementById("chat-box");
    const thumbnailElement = document.createElement("img");
    thumbnailElement.className = "thumbnail";
    thumbnailElement.src = thumbnailUrl;
    thumbnailElement.alt = "Thumbnail";
    thumbnailElement.onclick = () => {
        // 다음 페이지로 이동
        window.location.href = `mp2_page.html?image=${encodeURIComponent(thumbnailUrl)}&obj=${encodeURIComponent(objUrl)}&prompt=${encodeURIComponent(UserPrompt)}`;
    };
    chatBox.appendChild(thumbnailElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}


//오류가 발생했을 경우 오류 코드를 출력
function addMessageToChat(message, sender) {
    const chatBox = document.getElementById("chat-box");
    const messageElement = document.createElement("div");
    messageElement.className = "chat-message " + (sender === 'user' ? 'user-message' : 'bot-message');
    messageElement.textContent = message;
    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}

//로딩중 메세지 표시
function addLoadingMessage() {
    const chatBox = document.getElementById("chat-box");
    loadingMessageElement = document.createElement("div");
    loadingMessageElement.className = "chat-message loading-message";
    //해당 부분 추후 수정
    loadingMessageElement.textContent = "3D 모델 생성 중 입니다. 잠시만 기다려주세요.";
    chatBox.appendChild(loadingMessageElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}

//로딩이 완료되었을 경우 로딩 메세지 삭제
function removeLoadingMessage() {
    if (loadingMessageElement) {
        loadingMessageElement.remove();
        loadingMessageElement = null;
    }
}

//진행도 표시
function addProgressMessage() {
    const chatBox = document.getElementById("chat-box");
    progressMessageElement = document.createElement("div");
    progressMessageElement.className = "chat-message loading-message";
    progressMessageElement.textContent = "진행률: 0%";
    chatBox.appendChild(progressMessageElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}

//진행도 업데이트
function updateProgressMessage(progress) {
    if (progressMessageElement) {
        progressMessageElement.textContent = "진행률: " + progress + "%";
    }
}

//진행도 지우기
function removeProgressMessage() {
    if (progressMessageElement) {
        progressMessageElement.remove();
        progressMessageElement = null;
    }
}

//유저의 프롬프트를 입력받는 함수
function handleUserInput() {
    const promptInput = document.getElementById("prompt-input");
    const prompt = promptInput.value;
    if (prompt.trim() !== "") {
        addMessageToChat(prompt, 'user');
        generatePromptAndFetchResult(prompt);
        promptInput.value = "";
    }
}

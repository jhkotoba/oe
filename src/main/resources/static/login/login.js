// 폼 요소에서 name 속성(userId, password, loginButton, prefix)에 해당하는 컨트롤을 구조 분해할당으로 가져옴
const { userId, password, loginButton, prefix } = document.getElementById('loginForm').elements;

// 로그인 버튼에 클릭 이벤트 리스너 등록
loginButton.addEventListener('click', async () => {
	
	// 중복 클릭 방지를 위해 버튼 비활성화
	loginButton.disabled = true;
	
	try{
		// 로그인 처리 API 호출
		let response = await fetch(`/${prefix.value}/login/process`, {
	        method: "POST",	       
	        credentials: "include",
	        headers: {
	             "Accept": "application/json",
	             "Content-Type": "application/json"
	        },
	        body: JSON.stringify({
	            "userId": userId.value,
	            "password": password.value
	        })
	    });
		
		// 응답 상태 코드가 2xx가 아닌 경우 에러 처리
		if(!response.ok){
			throw new Error(`server error ${response.status}`);
		}
		
		// 현재 URL의 쿼리 문자열에서 'rtnUrl' 파라미터 추출
		const params = new URLSearchParams(window.location.search);
		const returnUrl = params.get('rtnUrl');
				
		if(returnUrl){
			// rtnUrl이 있으면 해당 URL로 리다이렉트
			window.location.href = returnUrl;	
		}else{
			// 없으면 기본 대시보드로 이동
			window.location.href = '/dashboard';
		}
		
	}catch(err){
		console.error(err);
		window.alert('로그인 실패하였습니다.');
	} finally {
		// 버튼 재활성화
		loginButton.disabled = false;
	}
});
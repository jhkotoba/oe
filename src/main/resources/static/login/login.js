const { username, password, loginButton } = document.getElementById('loginForm').elements;

loginButton.addEventListener('click', async event => {
	
	const subtle = window.crypto.subtle;
	
	// 키 생성 (256비트 AES-GCM)
	const key = await subtle.generateKey(
		{ name: 'AES-GCM', length: 256 },
		true, // 내보내기
		['encrypt','decrypt']
	);
	
	// 키를 raw ArrayBuffer로 내보내고 Base64 인코딩
	const rawKey = await subtle.exportKey('raw', key);
	const base64Key = btoa(String.fromCharCode(...new Uint8Array(rawKey)));
	
	// IV(초기화 벡터) 생성
	const iv = crypto.getRandomValues(new Uint8Array(12));
	const base64Iv = btoa(String.fromCharCode(...iv));
	
	// 암호화할 평문 준비
	const plaintext = new TextEncoder().encode(password.value);
	
	// 암호화
	const encryptedBuffer = await subtle.encrypt({name: 'AES-GCM', iv}, key, plaintext);
	const base64Ciphertext = btoa(String.fromCharCode(...new Uint8Array(encryptedBuffer)));
	
	console.log('base64Ciphertext:', base64Ciphertext);
	
});



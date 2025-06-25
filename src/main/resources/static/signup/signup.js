const { userId, password, confirmPassword, signupButton, cancelButton, prefix } =
  document.getElementById('signupForm').elements;

// 취소 버튼 클릭 시 뒤로가기
cancelButton.addEventListener('click', () => {
  history.back();
});

signupButton.addEventListener('click', async () => {
  signupButton.disabled = true;

  // 비밀번호 일치 체크
  if (password.value !== confirmPassword.value) {
    alert('비밀번호가 일치하지 않습니다.');
    signupButton.disabled = false;
    return;
  }

  try {
    const res = await fetch(`/${prefix.value}/signup/process`, {
      method: 'POST',
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        userId: userId.value,
        password: password.value
      })
    });
    if (!res.ok) throw new Error(`server error ${res.status}`);

    // 가입 성공 후 로그인 페이지로 이동
    window.location.href = `/${prefix.value}/login?signupSuccess=true`;
  } catch (err) {
    console.error(err);
    alert('회원가입에 실패하였습니다.');
  } finally {
    signupButton.disabled = false;
  }
});
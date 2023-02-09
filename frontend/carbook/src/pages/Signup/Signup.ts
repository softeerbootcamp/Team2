import { Component } from '@/core';
import './Signup.scss';
import car from '@/assets/images/car.svg';
import { push } from '@/utils/router/navigate';
import { EMPTYID, EMPTYPW, EMPTYNICKNAME } from '@/constants/errorMessage';

export default class SignupPage extends Component {
  template(): string {
    return /*html*/ `
    <div class = 'signup-container'>
    <header>
    <img src = ${car} class ='header-image'>
  <h1 class = 'header-logo'>CarBook</h1>
  <h3 class = 'header-message'>카북에 오신걸 환영합니다</h3>
  </header>
  <div class='signup-contents'>
    <form class ='input-form'>
      <div class = 'signup-id'> ID</div>

      <input type = 'email' placeholder='id를 입력해주세요' class ='input-box' name ='signupid' required autofocus/>
      <div class ='signup-password'> Password</div>
      <input type = 'password' class ='input-box' name = 'password' placeholder='비밀번호를 입력해주세요' maxlength='16'/>
      <div class ='signup-nickname'> Nickname</div>
      <input type = 'nickname' class ='input-box' name = 'nickname' placeholder='닉네임을 입력해주세요' maxlength='16'/>

      <button type = 'submit' class ='input-form-button'>회원가입</button>
    </form>
  </div>
  <footer>
    <h3 class ='footer-message'>계정이 이미 있으신가요?</h3>
    <h3 class ='footer-login'>로그인</h3>
  </footer>
  <div class = 'alert-modal'>오류 : 닉네임이 중복되었습니다.</div>
  </div>`;
  }

  setEvent(): void {
    const form = document.body.querySelector(
      '.signup-container .input-form'
    ) as HTMLFormElement;
    form?.addEventListener('submit', (e) => {
      e.preventDefault();

      const id = form.signupid.value.trim();
      const password = form.password.value.trim();
      const nickname = form.nickname.value.trim();

      const modal = document.body.querySelector('.alert-modal') as HTMLElement;

      if (isEmpty(id, password, nickname, modal)) return false;

      push('/login');
      return false;
    });
  }
}

function showErrorModal(modal: HTMLElement, errorMessage: string): void {
  if (modal.classList.contains('FadeInAndOut')) return;
  modal.innerHTML = errorMessage;
  modal.classList.toggle('FadeInAndOut');
  setTimeout(() => {
    modal.classList.toggle('FadeInAndOut');
  }, 2000);
}

function isEmpty(
  id: string,
  password: string,
  nickname: string,
  modal: HTMLElement
) {
  if (id === '') {
    showErrorModal(modal, EMPTYID);
    return true;
  }
  if (password === '') {
    showErrorModal(modal, EMPTYPW);
    return true;
  }
  if (nickname === '') {
    showErrorModal(modal, EMPTYNICKNAME);
    return true;
  }
  return false;
}

# i_stick
2018_2학기 캡스톤 프로젝트 i stick

I Stick Server Notification using Nodejs

<h1>Routing</h1>
<ul>/*  Login */
  <li> activity_login.xml  MainAcivity.java : Completed /login  :   로그인 화면 login이 완료되면 putExtra 로 no, id 전송</li>
</ul>
<ul>
/*  Regist  */
  <li> activity_register.xml RegistActivity.java : Completed  /register   :   회원가입  /check/id   :   ID 중복 체크</li>
</ul>
<ul>
/*  User  */
  <li> activity_user.xml UserActivity : yet /user   :   user 로그인 화면 /user/navigate : 길찾기 모드</li>
</ul>
<ul>
/*  Parent  */
  <li> activity_parent.xml ParentActiviy.java : Completed getExtra : no, id 받기  /parent :   parent 로그인 화면 userList 얻어와서 화면 설정</li>
  <li> activiy_edit_parent_info.xml  EditParentInfo.java : Completed  /parent/edit  : 내 정보 수정</li>
  <li> activity_parent_menu.xml ParentMenu.java : yet /parent/menu/id </li>
  <li> 담당 user추가 dialog : user id, pw입력하여 등록 : yet /parent/regist </li>
</ul>

<h1>2018-10-13 Hashing</h1>
<ul>
  <li>사용자 회원가입, 로그인, 개인정보 변경에서 password에 암호화 설정</li>
  <li>DB에 암호화되어 저장</li>
  <li>비동기적 진행으로 인한 로그인 과정 에러 해결</li>
</ul>

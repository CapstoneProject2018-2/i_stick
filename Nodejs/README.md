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

<h1>2018-10-16 Connect with Android Application</h1>
<ul>
  <li>Parnet화면에서 관리하려는 사용자(User)추가 기능 추가</li>
  <li>Istick ver1.0 에서 ver1.1 업데이트</li>
</ul>
<h1>2018-10-29</h1>
<ul>
  <li>클라우드 서버 외부 고정 ip 설정 - Android Server interface에 갱신</li>
  <li>lib디렉터리 생성 - 소스코드의 모듈화 정리</li>
  <li>app_iStick_server(main) 소스 수정</li>
</ul>
<h1>2018-10-30</h1>
<ul>
  <li>전체적인 쿼리문 수정 - 보안강화</li>
  <li>./lib/parent.reqLoc 추가, 위치기능 구현이후 테스트 해야함</li>
  <li>버그수정 ./lib/user.js</li>
  <li>버그수정 ./lib/parent.reqLoc</li>
  <li>안드로이드 서버통신작업 코드 생성</li>
  <li>./lib/parent.deleteUser 추가 및 안드로이드 소스 추가</li>
</ul>
<h1>2018-11-09</h1>
<ul>
  <li>보호자 사용자의 목적지 지정 기능 테스트 서버 추가 ./lib/parent/setDestination</li>
</ul>

<h1>2018-11-12</h1>
<ul>
  <li>./lib/parent/setDestination 기능 구현 완료</li>
  <li>./lib/user.user: 보호자가 지정해준 목적지 위치정보 수신기능 구현 - DB수정</li>
</ul>

<h1>2018-11-24</h1>
<ul>
  <li>FCM 기능 추가</li>
  <li>user.js 와 parent.js FCM추가</li>
  <li>register.js login.js의 수정</li>
</ul>

<h1>2018-11-24</h1>
<ul>
  <li>./lib내 query최적화 - 이중쿼리 수정, select수정으로 서버에서 이용할 뷰만 가져오게 수정</li>
  <li>checkId의 버그 수정, 안드로이드 RegistActivity와 함께 수정</li>
</ul>

<h1>2018-12-04</h1>
<ul>
  <li>setDestination 수정, 사용자에게 길을 지정해준 보호자로 전화하기 위한 parent mobile정보 제공</li>
</ul>


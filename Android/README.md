# i_stick
2018_2학기 캡스톤 프로젝트 i stick

Android Code Repository

<h1>2018-11-05</h1>
<ul>
  <li>Feedback: 사용자 등록시 비밀번호 확인 기능 추가</li>
</ul>

<h1>2018-11-13</h1>
<ul>
  <h2>SetPathActivity</h2>
  <ul>
    <li>POIItem 검색 기능과 검색POI로 목적지 지정, 서버연결 기능 추가</li>
  </ul>
  <h2>UserActivity</h2>
  <ul>
    <li>목적지 위치 정보 가져오기, 현재위치 기준으로 목적지까지의 경로 탐색 기능 추가</li>
    <li>탐색한 경로 저장 후, 현재위치기준으로 방향 제시 기능 추가</li>
    <li>경로정보를 블루투스 통신을 통한 아두이노로 전송 기능 추가</li>
  </ul>
</ul>

<h1>2018-11-20</h1>
<ul>
  <li>기존 블루투스 코드 수정, BluetoothSPP 이용</li>
  <li>Oblu 관련 class 병합</li>
</ul>

<h1>2018-11-21</h1>
<ul>
  <li>경위도 <-> 좌표 변환 알고리즘 추가</li>
  <li>Feedback: 주소검색시 나오는 null값 제거 (PoiListViewAdapter)</li>
</ul>

<h1>2018-11-24</h1>
<ul>
  <li>FCM 기능 추가, Service 클래스들의 추가와 FindPathTask의 위치 변경(Gradle추가)</li>
  <li>Debug: 이미 User의 목적지로 가능 경로가 있을 때, 보호자가 새로운 목적지 지정시 프로그램이 종료되던 에러 수정 - UserActivity</li>
</ul>

<h1>2018-11-26</h1>
<ul>
  <li>ServerInfo Interface에 permissions 추가, user_permissions(위치), parent_permissions(전화)</li>
  <li>HisLocActivity 버튼 implements</li>
</ul>

<h1>2018-11-27</h1>
<ul>
  <li>navigation 에 DeadReckoning 모듈 추가.</li>
  <li>UserActivity는 Oblu를 사용하지 않는 I Stick만을 이용한 네비게이션</li>
  <li>DeviceControlActivity에서 Oblu와 I Stick을 이용한 네비게이션</li>  
</ul>

<h1>2018-11-29</h1>
<ul>
  <li>MapCalculator 최적화를 위한 테스트 알고리즘 3개 추가 With...</li>
  <li>currenAltitude추가 MapCalculator에 사용</li>  
</ul>

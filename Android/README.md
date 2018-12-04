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

<h1>2018-11-30</h1>
<ul>
  <li>SetPathActivity Searching Algorithm 수정: 사용자의 이전거리에서 5km반경 지점들만 검색</li>
  <li>ActionBar Title이름 수정, string.xml에 title name 추가</li>
  <li>UI 소폭 수정: EditParentInfo 설명과 버튼, LoginActivity 로그인정보 텍스트, ParentActivity 관리 리스트</li>
</ul>

<h1>2018-12-01</h1>
<ul>
  <li>MapCalculator클래스 고도추가로 인한 버그 수정</li>
  <li>디버깅을 모드 위한 MapDrawer클래스 추가</li>
</ul>

<h1>2018-12-02</h1>
<ul>
  <li>MapCalculator클래스 고도추가로 인한 버그 수정</li>
  <li>디버깅을 모드 위한 MapDrawer클래스 추가</li>
  <li>MapDrawer클래서 마커가 잘 뜨지않던 에러 수정</li>
  <li>Navigation기능을 하기위한 스레드, 핸들러 별도 생성</li>
</ul>

<h1>2018-12-03</h1>
<ul>
  <li>Navigation기능 위한 스레드, 핸들러 에러 수정 완료</li>
  <li>Point형식이 아닌 LineString형식에 심하게 꺾인길에서 방향제시 Point생성을 위한 LineSeperator클래스 생성</li>
  <li>NaviDataLogger에 pathlist출력하여 LineSeperator 인자값 최적화 진행</li>
</ul>

<h1>2018-12-04</h1>
<ul>
  <li>실전 데모 테스트</li>
  <li>CompleteMode: 실제 출시 할 때의 앱<li>
  <li>DebuggingMode: 개발자용 디버깅 앱<li>
</ul>
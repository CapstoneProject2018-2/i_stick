# i_stick
2018_2학기 캡스톤 프로젝트 i stick

LineSeperator Data Logger

*_line분해_a_b_c.txt
a: SEPERATORABLE_LINEPOINT_NUM - LineString 에서 PathItem을 만들기위한 최소 Line을 구성하는 Point개수
b: INDEX_INTERVAL - 각도를 계산할 Point 묶음의 수
c: THRESHOLD_ANGLE - 임계 각, Math.PI/c

ver1: 모든 Seperatable line point를 찾아줌
ver2: Point 중복 제거
ver3: Debug divide by zero
ver4: Math.atan의 한계로 알고리즘 변경
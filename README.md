# 객체지향프로그래밍 팀 프로젝트 README

### 프로젝트 개요

프로젝트명 : 도서관 도서 대출·반납 관리 시스템

개발 목적 : 도서관 도서 대출·반납 관리 시스템을 만들면서 OOP를 실제 클래스 구조와 객체에 적용되도록 하는 연습을 위해 개발.

주요 기능 : 
1) 자료 등록 (도서, 잡지, 참고도서 분류)
2) 자료 검색 (1. 제목 저자로 검색, 2. 대소문자 무시, 3. 부분 일치 검색)
3) 회원 등록 (일반 회원, 학생 회원)
4) 대출 (회원의 도서 연체 여부, 대출 권수, 자료의 대출 여부 상태 검사)
5) 반납 (도서 연체 일수 계산하여 연체료 산출, 자료의 대출 여부 상태 복구)
6) 회원 대출 현황 및 이력 조회 
7) 연체 도서 목록 조회 (예상 연체료 포함하여 조회)
8) 날짜 설정 (시연 때 연체료 산출 등을 위한 기능)


### 팀 구성
팀원 : 2593033 이세준, 2593030 최현성

역할 분담 : 

이세준 (model 패키지, MemberPanel, OverduePanel, LoanTableModel, OverdueTableModel)


최현성 (service, exception, util/SampleDataFactory, MainFrame, BookPanel, AdminPanel, ItemTableModel, Dialogs, Main)


### 개발환경
java : JDK 21

intelliJ IDEA, Git, Github

라이브러리 :

java.time(일자, 일수 계산),

java.util, java.util.stream(자료와 회원 관리 및 검색),

javax.swing, java.awt(ui)

### 프로젝트 폴더 구조
src/com/library

|-- Main.java  : 객체 생성, 시연 데이터, 메인 창 띄우기

|-- model  : 도메인 객체 - 자료, 회원, 대출의 상태와 규칙이 있음. (자료, 회원, 대출 등과 같은 내용)

|-- service  : 업무 흐름 - 대출, 반납 등의 업무 흐름 담당. (오늘의 날짜 관리, 회원과 자료 등록 및 검색, 대출 반납 등의 업무 처리)

|-- exception  : 예외 상황 처리 (이미 대출 중인 책을 대출, 연체 중인 회원의 대출 시도 등 예외 상황에 대한 처리)

|-- util  : 시연용 데이터

|__ ui  : main 실행 시, 표시되는 창(ui)

### 실행 방법 설명
1) JDK, intelliJ IDEA 설치 및 저장소 내려받기(git clone https://github.com/churstuv/oop-library-system)
2) File → Project Structure → Project에서 Project SDK를 JDK [확인 필요: 버전] 으로 설정합니다.
   src 폴더가 소스 루트로 인식되는지 확인. 아니라면 src를 우클릭해 Mark Directory as → Sources Root로 지정.
3) 빌드
4) main 실행

### OOP 적용 내용과 설계이유 설명
1) **은닉성** (LibraryItem(status), Member(activeLoans)) : 자료의 상태는 markOnLoan()과 markAvailable() 로만 변경 가능하게 해서 잘못된 상태 변경 방지. 대출 목록은 읽기 전용.
2) **상속성** (Book, Magazine, ReferenceBook, GeneralMember, StudentMember) : 도서자료와 회원의 공통 속성과 기능을 부모 클래스에 두고, 대출 기간, 연체료, 최대 권수와 같은 종류별 차이만 하위 클래스에서 정함.
3) **다형성** (Magazine.calculateLateFee(), Member.getMaxLoanCount()) : 같은 메소드를 호출할 때 override 사용. 
4) **추상성** (Loanable(interface), LibraryItem, Member(abstract)) : 대출 서비스가 Loanable 을 통해 대출 가능 여부를 확인.

### 생성형 AI 활용
사용 도구 : Claude

활용 수준 : 이세준(1), 최현성(4)

주요 활용 내용 : 설계 검토, 클래스 구현, 오류 및 누락 사항 점검

### 기능 테스트 결과



### 객체 지향 설계 개선 내용 (초기, 최종의 변경 사항과 변경 이유)


## Chapter4 : HTTP 웹 서버 구현을 통해 HTTP 이해하기 

### 어려웠던 부분 
> 요구사항1 : http://localhost:8080/index.html로 접속했을 때 webapp 디렉토리의 index.html 파일을 읽어 클라이언트에 응답한다
  - index.html 파일 읽어서 byte 형태로 만들어준 뒤 DataOutputStream을 통해 response 넘기면 되지 않을까.. ?   
  - http://localhost:8080/index.html로 요청왔을 때 '/' 이후 부분에 파일명을 어떻게 가져오지 ??
   - connection.getInputStream() 디버거로 찍어보니 여기엔 없는 것 같다. (connection은 Socket 객체)
   - InputStream은 바이트 단위로 나타내기 때문에 디버거로 찍어도 원하는 값 볼 수 없다. <br>
   (https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html 참고)
   - 따라서, InputStreamReader 활용하여 byte array인 InputStream을 디코딩하고 BufferdReader 이용해서 라인 단위로 읽는다.
  
 
### 배운 것
- Java I/O (InputStream, InputStreamReader, BufferedReader 등 - [참고 링크](https://st-lab.tistory.com/41)) 

### 인상 깊었던 말


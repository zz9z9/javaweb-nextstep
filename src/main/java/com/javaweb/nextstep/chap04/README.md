## Chapter4 : HTTP 웹 서버 구현을 통해 HTTP 이해하기 

### 어려웠던 부분 
> 요구사항1 : http://localhost:8080/index.html로 접속했을 때 webapp 디렉토리의 index.html 파일을 읽어 클라이언트에 응답한다
  - index.html 파일 읽어서 byte 형태로 만들어준 뒤 DataOutputStream을 통해 response 넘기면 되지 않을까.. ?   
  - http://localhost:8080/index.html 라는 요청왔을 때 '/' 이후 부분에 파일명을 어떻게 가져오지 ??
  - connection.getInputStream() 디버거로 찍어보니 여기엔 없는 것 같다. (connection은 Socket 객체)
  - 값이 없던게 아니라 InputStream은 바이트 단위로 나타내기 때문에 디버거로 찍어도 원하는 값 볼 수 없었던 것. <br>
   (https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html 참고)
  - 따라서, 클라이언트가 요청한 html 파일이 무엇인지 알기 위해 InputStreamReader 활용하여 byte array인 InputStream을 디코딩하고 BufferdReader 이용해서 라인 단위로 읽는다.
  - 최종적으로 아래와 같은 메서드로 구현
```java
 // 클라이언트가 요청한 html 파일 이름 가져온다
    private String getRequestHtmlName(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String requestInfo = bufferedReader.readLine();

        if(!requestInfo.contains(".html")) {
            return null;
        }

        String htmlPage = requestInfo.split(" ")[1];

        return htmlPage.substring(1); // 맨 앞에 '/' 지우기 위함
    }
    // http response를 위해 파일을 byte로 변환(절대경로 ver)
    private byte[] convertHtmlToByte(String fileName) throws IOException {
        String rootPath = System.getProperty("user.dir");
        String filePath = rootPath + "/webapp/" + fileName;
        File file = new File(filePath);

        return Files.readAllBytes(file.toPath());
    }  
    // http response를 위해 파일을 byte로 변환(상대경로 ver - 책 참고)
    private byte[] convertHtmlToByte(String fileName) throws IOException {
        String filePath = "./webapp/" + fileName;
        File file = new File(filePath);

        return Files.readAllBytes(file.toPath());
    }  
```
### 배운 것
- Java I/O (InputStream, InputStreamReader, BufferedReader, FileReader 등 - [참고 링크](https://st-lab.tistory.com/41)) 
- 상대경로를 사용할 경우, ./ (현재 위치)는 bin, src 폴더를 포함하는 해당 자바 프로젝트 폴더의 위치이다.

### 인상 깊었던 말로


# ai-mcp
此工程使用java-mcp-sdk，来创建mcp server和mcp client，测试mcp协议功能和流程

## ai-mcp-serverspring(mcp server远程服务)
此mcp server使用spring mvc框架，传输协议使用streamable http，作为远程mcp server服务

### ai-mcp-serverspring提供的能力 
#### tool
read_file 读取指定路径的文本文件内容

参数：filePath 文件路径

注意：除了读取文件内容外，其中还使用了logging能力，模拟文件读取进度，进度信息从server发送到client

#### resource
my-picture 个人图片

### 编译运行
~~~shell
cd ai-mcp-serverspring
#编译打包mcp server
mvn clean package
#运行mcp server
java -jar target/ai-mcp-serverspring-1.0-SNAPSHOT.jar
~~~

### 访问入口
http://127.0.0.1:8080/mcp

## ai-mcp-serverlocal(mcp server本地服务)
此mcp server传输协议使用stdio，mcp server跟mcp client部署在同一个主机上，server作为client子进程启动并提供服务(mcp协议规定)

### ai-mcp-serverspring提供的能力
#### tool
read_local_file 读取本地指定路径的文本文件内容

参数：filePath 文件路径

### 编译运行
~~~shell
cd ai-mcp-serverlocal
#编译打包
mvn clean package
#jar包地址:target/ai-mcp-serverlocal-1.0-SNAPSHOT.jar
~~~
注意ai-mcp-serverlocal，是由mcp-client带起来作为其子进程来提供服务的；这里先不运行


## ai-mcp-client(mcp client客户端)
mcp client客户端，用来连接mcp server，调用mcp server能力

在AI应用中AI agent就是通过mcp client，来使用mcp server能力

### McpClientConnLocal连接本地mcp server
mcp client连接ai-mcp-serverloal服务；

执行tool:read_local_file,并输出内容

修改McpClientConnLocal类中ai-mcp-serverlocal-1.0-SNAPSHOT.jar的路径为你自己打包出来后的实际路径
~~~
        ServerParameters params = ServerParameters.builder("java")
                .args("-jar", "/Users/liyu/develop/wp/java-wp/self-projects/ai-learn/ai-mcp/ai-mcp-serverlocal/target/ai-mcp-serverlocal-1.0-SNAPSHOT.jar")
                .build();
~~~

编译运行
~~~
cd ai-mcp-client

#编译打包
mvn clean package

#运行client
java -cp target/ai-mcp-client-1.0-SNAPSHOT.jar com.ai.mcp.client.McpClientConnLocal

#执行后，在client控制台能看到mcp server tool返回的内容；
~~~



### McpClientConnRemote连接远程mcp server
mcp client连接ai-mcp-serverspring服务

执行tool:read_file,并输出内容

编译运行
~~~
cd ai-mcp-client

#编译打包
mvn clean package

#运行client
java -cp target/ai-mcp-client-1.0-SNAPSHOT.jar com.ai.mcp.client.McpClientConnRemote

#执行后，在client控制台能看到mcp server tool返回的内容；
~~~

## 通过postman/curl访问 ai-mcp-serverspring
除了mcp client访问远程mcp server，我们也可通过postman/curl工具来访问；

~~~http
###初始化streamable http连接，mcp server会返回 header：mcp-session-id，记住这个值，后续所有请求都需要使用
POST /mcp HTTP/1.1
Host: localhost:8080
Accept: application/json,text/event-stream
Content-Type: application/json
Content-Length: 213

{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "initialize",
  "params": {
    "protocolVersion": "2025-06-18",
    "capabilities": {},
    "clientInfo": {
      "name": "postman",
      "version": "1.0"
    }
  }
}

###streamable http初始化完成
POST /mcp HTTP/1.1
Host: localhost:8080
Accept: application/json,text/event-stream
Content-Type: application/json
mcp-session-id: 4b3059f1-9952-4de3-897a-683b11eb82c8
Content-Length: 61

{
  "jsonrpc":"2.0",
  "method":"notifications/initialized"
}

###获取mcp server tools列表
POST /mcp HTTP/1.1
Host: localhost:8080
Accept: application/json,text/event-stream
Content-Type: application/json
mcp-session-id: 4b3059f1-9952-4de3-897a-683b11eb82c8
Content-Length: 56

{
  "jsonrpc":"2.0",
  "id":2,
  "method":"tools/list"
}

### 调用mcp servr tools 
POST /mcp HTTP/1.1
Host: localhost:8080
Accept: application/json,text/event-stream
Content-Type: application/json
mcp-session-id: 4b3059f1-9952-4de3-897a-683b11eb82c8
Content-Length: 181

{
  "jsonrpc":"2.0",
  "id":3,
  "method":"tools/call",
  "params":{
      "name":"read_file",
      "arguments":{
          "filePath":"/Users/liyu/Downloads/123.txt"
      }
  }
}
~~~

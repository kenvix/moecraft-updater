# MoeCraft 通用更新器

**注意**：本软件为开源软件，但并非自由软件。使用须联系本人获得授权，否则不得使用本软件或其任何组件。

# 如何将本项目部署在您自己的服务器

## 1.准备工作

1.首先，您应该有一台能够正常访问的网页服务器，端口不定，但应当使用**HTTP Over TLS(HTTPS)协议**并且TLS协议至少应为**TLS 1.2**。<br />
2.您应该安装了**Java JDK**且版本至少为**1.8.0_221**以上。<br />
3.安装了IDEA(可选)(IDE)<br />
4.安装了Java Scene Builder(Javafx)<br />

## 2.修改文件
#### 1.```src\main\java\net\moecraft\generator\Environment.java```
```java
...
    private final static Class[] repoManager = { AccountCenterRepoManager.class, LocalIntegratedRepoManager.class };
    private final static String dnsRepoDomain = "updater-repo.moecraft.net";//不确定有何用处，故不作更改
    /*
    节点仓库，如果你是群组服或者有非同一时间同步多个客户端需求，你也可以把他当做客户端列表。
    该文件应为json格式，且不应该有注释。地址则为您的网页服务器（即客户端文件存放服务器）
     */
    private static final String repoManagerURL = "https://accounts.moecraft.net/repo";//节点仓库，如果你是群组服或者有非同一时间同步多个客户端需求，你也可以把他当做客户端列表。
    private final static String appName = "MoeCraft Toolbox"; //应用名，你应该根据个人需求修改
    private final static String outJsonName = "MoeCraft.json";//作为生成器使用时输出的json文件，可改可不改
...
```
除此之外，该文件还有一处需要更改。
```java
...
    static void loadEnvironment(final CommandLine cmd) throws IOException {
    ...
        basemoecraftDir = new File(cmd.hasOption('p') ? cmd.getOptionValue('p') : "./moecraft");//客户端输出文件夹，你应该按自己的需求修改它
        generatorConfigFile = new File(cmd.hasOption('c') ? cmd.getOptionValue('c') : "./generator_config.json");
        basemoecraftPath = basemoecraftDir.getCanonicalPath().replace('\\', '/');
        updateDescription = cmd.hasOption('i') ? cmd.getOptionValue('i') : "";
        isUpdater = !cmd.hasOption('g');
        updateVersion = cmd.hasOption('l') ? cmd.getOptionValue('l') : "1.0";
    }
...
```

#### 2.```src\main\java\net\moecraft\generator\updater\repo\LocalIntegratedRepoManager.java```

```java
package net.moecraft.generator.updater.repo;
public class LocalIntegratedRepoManager implements RepoManager {
	    @Override
	        public Repo[] getRepos() throws Exception {
			        return new Repo[]{
					/*
					这里是如果未获取到节点列表默认显示的节点
			        第一个数字0代表了该节点的序号，序号也代表了显示顺序，该数字最小为0,最大未测试，但推荐不超过10
					第一个逗号后的内容是客户端下载地址的根目录。别忘了地址要用引号引住
					第二个逗号后的内容是名字。同上，别忘了引号
					第三个逗号后的内容是填写Environment.java时里面的生成器输出json
					第四个逗号后的内容是显示的信息
					*/
					new Repo(0, "https://accounts.moecraft.net/tech", "tech", "tech.json", "[推荐] MoeCraft 格雷服")
					/*
					如上面的示例
					当未获取到节点列表时就会输出
					[0] [推荐] moecraft 格雷服
					并自动从https://accounts.moecraft.net/tech下载tech.json与其他客户端文件
					*/
				};
				}
		}
```
#### 3.```src\main\java\net\moecraft\generator\updater\ui\cli\CommandLineUI```**（可选）**

此处为当以命令行模式(-cli)启动时输出的界面，一般用户不会用到此功能，根据个人需求修改即可。

#### 4.```src\main\resources\net\moecraft\generator\updater\ui\gui\nodeselect.fxml```与该文件夹下的favicon.png

此处为当以GUI模式下启动时输出的界面，使用Java Scene Builder按照需求编辑即可。

#### 5.网页服务器下的repo

此处为节点列表，要注意的是，json是不允许注释的，因此当您部署时需要删掉注释。

```json
[
	[
		0,//显示的顺序和序号
		"https:\/\/moecraft.net\/tech\/",//客户端存放的目录
		"tech",//名字
		"MoeCraft.json",//填写Environment.java时里面的生成器输出json
		"[\u65b0\u73a9\u5bb6\u63a8\u8350]MoeCraft \u79d1\u6280\u670d"//输出的内容，要注意的是中文必须转换成Unicode编码
	],//当这不是最后一个节点时需要填写逗号，若是，则必须删去逗号
	[
		1,//显示的顺序和序号
		"https:\/\/moecraft.net\/old\/",//客户端存放的目录
		"old",//名字
		"MoeCraft.json",//填写Environment.java时里面的生成器输出json
		"[\u767d\u540d\u5355]MoeCraft \u517b\u8001\u670d"//输出的内容，要注意的是中文必须转换成Unicode编码
	]//当这是最后一个节点时不应该填写逗号，若只有一个节点也如此。
]
```
至此，要修改的文件已经全部修改完成。

## 3.生成更新文件

生成器将会依照生成文件来生成客户端。本文简略介绍了生成器配置文件的书写方法。
示例的生成配置见wiki目录下的 `generator_config.json`。

#### 生成配置文件说明
```json
{
  "description": "MoeCraft", //版本说明，可以书写任意内容。
  "version": "5.1", //版本号，更新器不会将其作为检测依据，仅用作显示
  "object_size": 3145727, //文件对象分块阈值。超过此大小的文件将会被切成多个块，单位：字节
  "name_rule": "%s/%s-%d.txt", //object文件命名规则，请勿随意修改
  "output_json": "../po3.json", //输出JSON路径
  "moecraft_dir": "PO3", //游戏所在目录
  "synced_dirs": [ //强制同步的文件夹列表，位于这里的文件夹会被强制同步到与服务器相同的状态
    "HMCLData/Library", //请勿将 .minecraft/config 文件夹设为强制同步，否则用户运行更新器会丢失所有minecraft mod 设置。您应该单独设置需要同步哪些config文件
    ".minecraft/versions",
    ".minecraft/mods",
    ".minecraft/scripts",
    ".minecraft/config/cofh",
    ".minecraft/config/unidict"
  ],
  "synced_files": [ //强制同步的文件列表
    "Launcher.jar" //请在这里声明需要与服务器强制同步的config文件
  ],
  "default_files": [ //默认文件，仅当客户端没有这些文件时才从服务器上下载
    "HMCLData/hmcl.json"
  ],
  "default_dirs": [  //默认目录，仅当客户端没有这些文件时才从服务器上下载
    
  ],
  "excluded_files": [ //生成器会跳过符合这些特征的文件，可以使用通配符
    "*.class",
    "*.log"
  ],
  "excluded_dir": [ //生成器会跳过这些文件夹，不可以使用通配符
    ".minecraft/crash-reports",
    ".minecraft/logs",
    ".minecraft/saves",
    ".minecraft/journeymap",
    ".minecraft/local"
  ]
}
```

**注意：** json是不可以有注释的，您应当在编写完成后删除所有注释。

#### 使用方法
1. 将上述文件存放到与更新器相同的目录，然后打开命令提示符(终端)，运行：
```batch
java -jar 构建出的jar文件 -g -c 指定的配置文件
```

2. 打开 Deployment 文件夹，将此文件夹的内容直接全部上传到 填写repo时输入的目录

**以PO3为例**

1. 把游戏放到PO3目录    
2. 单独编写gen_po3.json，可以看到json里有一行output_json，表示这个输出json的文件名    
3. 启动生成器，参数 `java -jar generator-2.0-SNAPSHOT.jar -g -c gen_po3.json -p PO3`      
      表示从PO3目录进行生成，配置文件为gen_po3.json
4. 把Deployment目录和po3.json丢上去即可    

如果要生成omni，同理    
直接在这里建一个Omni目录，然后单独编写gen_omni.json，修改output_json    
启动生成器，参数 java -jar generator-2.0-SNAPSHOT.jar -g -c gen_omni,json -p omni   
Deployment里的文件可以共享，多个包只需下载一套文件     

其中Deployment的内容只添加 不要覆盖
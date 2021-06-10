# IMG2PDF 開發日誌
## 開發環境

### IntelliJ IDEA 環境

#### 使用插件

- [PlantUML integration](https://plugins.jetbrains.com/plugin/7017-plantuml-integration)
- [PlantUML Parser](https://plugins.jetbrains.com/plugin/index?xmlId=plantuml-parser)
- [IdeaVim](https://plugins.jetbrains.com/plugin/164-ideavim)
- [IntelliJ IDEA Properties Sorter](https://plugins.jetbrains.com/plugin/9883-intellij-idea-properties-sorter)

#### 編譯器

- jdk1.8.0_291

#### Maven設置

User setting file:  **C:\Users\%USERPROFILE%\\.intellij.m2\settings.xml**

Local repository:  **C:\Users\%USERPROFILE%\.intellij.m2\repository**



### Maven環境

- 版本：apache-maven-3.8.1

#### 使用依賴

- org.apache.pdfbox 2.0.21
- junit 4.13.1
- info.picocli



### 環境變數

```
M2_HOME=C:\bin\apache-maven-3.8.1
JAVA_HOME=C:\Program Files\Java\jdk1.8.0_291
JRE_HOME=C:\Program Files\Java\jre1.8.0_291
Path=%M2_HOME%\bin\;%JAVA_HOME%\bin\;
```


# IMG2PDF

## 說明
IMG2PDF 是一個可以將數張圖片合併/轉檔成一個pdf檔案。支援一次轉換多個PDF，利用**一個目錄即為一個PDF檔**的概念進行批次作業，這項功能適合使用在需要轉換多個PDF的工作上。

在執行IMG2PDF之前，你需要先指定哪個目錄中的圖片需要轉換成PDF，新建一個**文字檔**，裡面條列著要轉換成PDF的目錄路徑。最後，執行IMG2PDF，將個別條列的目錄路徑裡的多張圖片轉為PDF。



## 使用方法

下載以編譯釋出的二進位``img2pdf.exe``，或是下載``img2pdf.jar``，下載完成後開啟命令提示字元(cmd)，並移動到含有``img2pdf.jar``或是``img2pdf.exe``的目錄底下。

### 開啟方式

#### JAR

```shell
java -jar "img2pdf.jar" [參數...]
```

#### EXE

```shell
chcp 65001
img2pdf.exe [參數...]
```

### 參數

使用``--help``獲得更多幫助

```shell
D:\>img2pdf.exe --help
```

```shell
D:\>img2pdf.exe convert --help
```



### 例子

資料夾結構:

```
D:/
├─ folder_list.txt
├─ any_folder/
│  ├─ comic/
│  │  ├─ page1.png
│  │  ├─ page2.png/
│  │  ├─ page3.png/
├─ img2pdf.exe
```

##### img2pdf.exe

主程式

##### comic 資料夾

裡面包含三個圖檔，將會被合併至PDF頁面。

##### folder_list.txt

內容如下，檔案必須使用UTF-8編碼，不可含UTF-8 BOM標頭。

```
D:\any_folder\comic\
```

開啟cmd輸入以下命令

```
chcp 65001
img2pdf.exe convert -pz "A4" -pdst "D:\outputs\<NAME>.pdf" "D:\folder_list.txt" 
```



轉檔完成後，``D:\outputs\comic.pdf``即為包含comic資料夾內的三個png的pdf檔，PDF頁面大小為**A4**。

變動後的資料夾結構

```
D:/
├─ outputs/
│  ├─ comic.pdf
├─ folder_list.txt
├─ any_folder/
│  ├─ comic/
│  │  ├─ page1.png
│  │  ├─ page2.png/
│  │  ├─ page3.png/
├─ img2pdf.exe
```



## 設定

新建檔案``config.properties``在和``img2pdf.exe``一樣的資料夾下

使用記事本輸入

```
dirlist-read-charset=[charset]
language=[language]
```

### 語言

將語言設定成中文(台灣繁體)

```
language=zh-TW
```

### 設定檔案目錄編碼方式

將編碼方式設定成UTF-8。如果使用UTF-8作為檔案讀取的編碼方式，參考上個例子，建議設定``folder_list.txt``的檔案編碼方式成UTF-8，以防止亂碼的產生。

```
dirlist-read-charset=UTF-8
```






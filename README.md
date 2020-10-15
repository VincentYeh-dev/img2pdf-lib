# IMG2PDF

## description
IMG2PDF is the tool to merge multiple image files or convert single image file to PDF file.

IMG2PDF is the batch processing program.
It support to convert the directory which contain multiple images into a PDF File.And,as stated above,IMG2PDF is the batch processing program,so it's avaliable to repeat above conversion by creating a list file which contain multiple **absolute path of directory**.


## Usage

First create a text file which contain single or multiple **line of absolute path of directory** and **save it as UTF-8 encoding-type**.

**dirlist.txt**
```
path\to\IMG2PDF\sample\walk-animation\image-sources
```

Now,we have a directory path list from dirlist.txt.All we have to do is create a java file and get started.

**sample java code**
```java=
String arguments="-sz A4 "
         + "-s NUMERTIC "
         + "-a CENTER|CENTER "
         + "-odr INCREASE "
         + "-lo tasklist.xml "
         + "-d $NAME.pdf "
         + "dirlist.txt";
		
        new TaskListCreator(arguments);
        new TaskProcessor("tasklist.xml");
```

### How it work

#### Task
Task is the core element of this program. It can't be call or run but it contain all nessesary arguments of conversion.
**"One Task pair one PDF file"**,one task only can generate one pdf.

#### TaskList
TaskList is the collection of Task.


#### TASKCREATOR

TASKCREATOR will convert **dirlist.txt** to **tasklist.xml**.
Now,tasklist.xml contain the TaskList,and the pre-work of conversion has done.

Use "-h" to understand the meaning of arguments.

**java code**
```java=
    new TaskListCreator("-h");
```

**output**
```
usage: TASKCREATOR [-h] [-s {NONE,NAME,DATE,NUMERTIC}]
                   [-odr {NONE,INCREASE,DECREASE}]
                   [-sz {A0,A1,A2,A3,A4,A5,A6,LEGAL,LETTER,DEPEND_ON_IMG}]
                   [-ownpwd ownerpassword] [-usepwd userpassword]
                   [-a TopBottom|LeftRight] [-d destination]
                   [-lo destination] [source [source ...]]

Create PDF Task

positional arguments:
  source                 File to convert

named arguments:
  -h, --help             show this help message and exit
  -s {NONE,NAME,DATE,NUMERTIC}, --sortby {NONE,NAME,DATE,NUMERTIC}
                         Merge all image files in Folder
  -odr {NONE,INCREASE,DECREASE}, --order {NONE,INCREASE,DECREASE}
                         order   by   increasing(0,1,2,3)   or   decreasing
                         (3,2,1,0) value
  -sz {A0,A1,A2,A3,A4,A5,A6,LEGAL,LETTER,DEPEND_ON_IMG}, --size {A0,A1,A2,A3,A4,A5,A6,LEGAL,LETTER,DEPEND_ON_IMG}
                         PDF each page size.
                         type DEPEND to set each  page  size depend on each
                         image size
  -ownpwd ownerpassword, --owner_password ownerpassword
                         PDF owner password
  -usepwd userpassword, --user_password userpassword
                         PDF user password
  -a TopBottom|LeftRight, --align TopBottom|LeftRight
                         alignment of page of PDF.
  -d destination, --destination destination
                         destination of converted file
  -lo destination, --list_output destination
                         Output task list(*.XML)
                         
```



#### TaskProcessor

TaskProcessor implement tasks in **tasklist.xml**.
Each task in TaskList will be converted to PDF file.

**java code**
```java=
    new TaskProcessor("tasklist.xml");
```
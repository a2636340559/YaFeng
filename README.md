# YaFeng
“雅风”古诗词APP
/android/YaFeng为app的源代码
/server/yafeng为后端的源代码
/images为功能截图

# 总体技术框架图如下：
![image](https://github.com/a2636340559/YaFeng/blob/master/images/1.png)
<br>
# 功能结构图如下：
![image](https://github.com/a2636340559/YaFeng/blob/master/images/function.PNG)

# 功能实现：
<br>

## 1、每日诗词推荐
App首页上方会根据每天的天气情况或所处位置推荐一句符合情况的诗句，同时每天会随机推荐一首诗给用户。
![image](https://github.com/a2636340559/YaFeng/blob/master/images/homePage.png)
<br>

## 2、诗词搜索
用户可以在首页搜索栏中输入作者或诗名或诗句进行搜索相应古诗。
![image](https://github.com/a2636340559/YaFeng/blob/master/images/search.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/searchPage.png)
<br>
    
## 3、诗词分类
按诗词的类型、朝代、写作内容对诗词进行分类，用户可点击首页左上角的图标查看所有的诗词分类，可选择相应的分类标签，点击确认，会根据所选标签进行筛选，并显示诗词列表。
![image](https://github.com/a2636340559/YaFeng/blob/master/images/sortPage.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/sortSearch.png)
<br>
    
## 4、诗词详情
在首页用户可以点击每日诗词右上角的查看详情进入诗词详情页，而在诗词列表中点击相应的列表项，则会进入该诗词的详情页，详情页中包含诗词正文、诗词分类、作者详情及诗词的赏析、翻译、注释、参考文献。若数据库中没有该诗词的详细信息，则会在app内部打开古诗词网的网页，用户可在古诗词网上进行搜索。
![image](https://github.com/a2636340559/YaFeng/blob/master/images/detailPage1.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/detailPage2.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/detailPage3.png)
<br>
    
## 5、诗词收藏
在登录之后用户可在每首诗的详情页中点击右上角的图标对诗词进行收藏。收藏之后，在首页点击右上角的图标在弹出的子菜单中选择点击“我的收藏”，则会显示收藏列表。若用户未登录，点击收藏相关的功能将会直接跳转至登录页面。
![image](https://github.com/a2636340559/YaFeng/blob/master/images/storagePage1.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/storagePage2.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/storagePage3.png)
<br>
    
## 6、背诵测试
在每首诗的下方会有测试按钮，若用户已记住该首诗，可点击进行测试，系统会根据诗词正文自动生成填空内容。若用户作答正确则作答内容显示绿色，反之显示红色。
![image](https://github.com/a2636340559/YaFeng/blob/master/images/testPage1.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/testPage2.png)
![image](https://github.com/a2636340559/YaFeng/blob/master/images/testPage3.png)

## 7、AI诗人
用户可输入需要创作的主题词，选择创作诗词的风格（律诗或藏头）和类型（五言或七言），系统将自动生成一首诗返回并显示。
![image](https://github.com/a2636340559/YaFeng/blob/master/images/aipoetry.jpg)


# bihu #

### APP简要介绍 ###

##### 背景：一款简单的问答APP

##### 功能：目前实现了api全部接口功能
![](https://github.com/dr-chene/bihu/blob/master/bihu_login_plus.gif) ![](https://github.com/dr-chene/bihu/blob/master/bihu_setting_plus.gif) 
![](https://github.com/dr-chene/bihu/blob/master/bihu_question_plus.gif) ![](https://github.com/dr-chene/bihu/blob/master/bihu_answer_plus.gif)

注册，登录 

修改头像（可拍照或从本地选择相片）修改密码

刷新问题,发布问题~~一直500~~

发布回答~~（发布文字回答会500，图片没问题）~~ 刷新回答 收藏~~一直500~~ 取消收藏 采纳 点赞问题，回答 取消点赞 踩问题，回答 取消踩

因为个人技术与时间有限，因此发布回答与问题时只可上传一张图片，且存在回答问题后无法自动刷新成功的bug，会严重影响使用体验~~i am so vegetable~~。

##### 使用步骤：首次进入APP若没有账号则注册一个账号，注册成功或登录成功即可正常使用该APP，账号会有一个初始头像，可再设置中修改上传头像（暂不可对选择的图片进行缩放，裁剪等功能），可通过下拉刷新问题，上拉加载更多，点击问题后可进入详情界面，在这里可对问题进行回答，点赞，点踩，收藏，可通过下拉对回答进行刷新。回答问题时，可附带一张图片，文字内容或图片有一即可。当然，你也可以发布自己的问题，文字标题，内容是必需的，图片可有可无。也拥有自己的收藏列表，已发布问题列表~~因为收藏会500，没有测试过~~。

### 使用到的比较重要的技术及知识点 ###

##### Material Design: Toolbar,AppBarLayout,DrawerLayout,NavigationView,FloatingActionButton,CoordinatorLayout,CardView,SwipeRefreshLayout 来源：第一行代码

##### Glide: 用于加载图片

##### 七牛云：图床

##### 网络请求，RecyclerView，SQLite数据库，接口回调，自定义CircleImageView 
##### View动画
##### Activity过渡动画
##### 第三方库：glide，七牛云所需的一系列库
~~想尝试使用MVP结构，但对我来说还有点困难~~

### 心得体会 ###
    学无止境：曾经有一段时间感觉自己有一点无事可做，现在才发现自己当时是有多么的愚蠢，如果想要成为一名合格的开发者，要学的其实非常多，根本不会有空闲的时间，之所以之前会有这种感觉，只能说我修行还不够
    算法的重要性：在编程的过程中，我曾一度感觉有种与算法脱离的感觉，直到我为了如何算出请求的页数与总数而头疼时才发现了问题的严重性虽然因为菜最后还是没有找到好的解决方案
    坚定了我在Java与Android方面学习下去的决心：计算机包括了太多的方面，比起学得多还不如学得深，于是终于下定决心，在这两方面深造，希望能在毕业这前成为一个合格的开发者
    编程虽然充满了各式各样的bug和令人崩溃的瞬间，但，真的很有趣
说实话，一开始做app的时候，脑袋里一片空白，没有半点app的雏形，现在能做成这样，可以说是很不容易了~~感动菜鸡（我）~~。~~虽然还没有做到最好多少让人有点遗憾~~

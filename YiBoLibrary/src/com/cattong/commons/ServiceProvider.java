package com.cattong.commons;

public enum ServiceProvider {

	None(-1, null, ServiceProvider.CATEGORY_SOCAILCAT, null), // 空，仅用于非SP请求或无需指定SP的情况
	SocialCat(ServiceProvider.SP_SOCIALCAT, "Shejiaomao.com", ServiceProvider.CATEGORY_SOCAILCAT, "Shejiaomao.com"),
	
	//微博
	Sina(ServiceProvider.SP_SINA, "新浪微博", ServiceProvider.CATEGORY_WEIBO, "积分邦"),
	Sohu(ServiceProvider.SP_SOHU, "搜狐微博", ServiceProvider.CATEGORY_WEIBO, "积分邦"),
	NetEase(ServiceProvider.SP_NETEASE, "网易微博", ServiceProvider.CATEGORY_WEIBO, "积分邦"),
	Tencent(ServiceProvider.SP_TENCENT, "腾讯微博", ServiceProvider.CATEGORY_WEIBO, "jifenbang_net"),
	Twitter(ServiceProvider.SP_TWITTER, "Twitter", ServiceProvider.CATEGORY_WEIBO, "jifenbang"),
	Fanfou(ServiceProvider.SP_FANFOU, "饭否", ServiceProvider.CATEGORY_WEIBO, "积分邦"),

	//SNS
	RenRen(ServiceProvider.SP_RENREN, "人人网", ServiceProvider.CATEGORY_SNS, ""),
	KaiXin(ServiceProvider.SP_KAIXIN, "开心网", ServiceProvider.CATEGORY_SNS, ""),
	QQZone(ServiceProvider.SP_QQZONE, "QQ空间", ServiceProvider.CATEGORY_SNS, ""),
	Facebook(ServiceProvider.SP_FACEBOOK, "Facebook", ServiceProvider.CATEGORY_SNS, "");

	private ServiceProvider(int spNo, String spName, String spCategory, String officalName) {
		this.spNo = spNo;
		this.spName = spName;
		this.spCategory = spCategory;
		this.officalName = officalName;
	}

	/** 服务提供商编号 */
	private int spNo;
	private String spName;
	private String spCategory;
	private String officalName;

	public int getSpNo() {
		return spNo;
	}

	public String getSpName() {
		return spName;
	}

	public String getSpCategory() {
		return spCategory;
	}

	public String getOfficalName() {
		return officalName;
	}

	public static final int SP_SOCIALCAT       = 30;     //shejiaomao.com的SP编号
	
	//微博平台编号
	public static final int SP_SINA            = 1;     // 新浪的SP编号
	public static final int SP_SOHU            = 2;     // 搜狐的SP编号
	public static final int SP_NETEASE         = 3;     // 网易的SP编号
	public static final int SP_TENCENT         = 4;     // 腾讯的SP编号
	public static final int SP_TWITTER         = 5;     // 推特的SP编号
	public static final int SP_FANFOU          = 6;     // 饭否的SP编号

	public static final int SP_RENREN          = 21;     // 人人网的SP编号
	public static final int SP_KAIXIN          = 22;     // 开心网的SP编号
	public static final int SP_QQZONE          = 23;     // QQ空间的SP编号
	public static final int SP_FACEBOOK        = 24;     // Facebook的SP编号

	public static final String CATEGORY_WEIBO = "weibo";
	public static final String CATEGORY_SNS = "sns";
	public static final String CATEGORY_SOCAILCAT = "socialcat";

	public static ServiceProvider getServiceProvider(int spNo){
		ServiceProvider sp = null;
		switch(spNo){
		case SP_SOCIALCAT:
			sp = SocialCat;
			break;
		case SP_SINA:
			sp = Sina;
			break;
		case SP_SOHU:
			sp = Sohu;
			break;
		case SP_NETEASE:
			sp = NetEase;
			break;
		case SP_TENCENT:
			sp = Tencent;
			break;
		case SP_TWITTER:
			sp = Twitter;
			break;
		case SP_FANFOU:
			sp = Fanfou;
			break;
		case SP_RENREN:
			sp = RenRen;
			break;
		case SP_KAIXIN:
			sp = KaiXin;
			break;
		case SP_QQZONE:
			sp = QQZone;
			break;
		case SP_FACEBOOK:
			sp = Facebook;
			break;
		default:
			sp = None;
			break;
		}
		return sp;
	}

	public boolean isSns() {
		return CATEGORY_SNS.equals(spCategory);
	}

	public boolean isWeibo() {
		return CATEGORY_WEIBO.equals(spCategory);
	}
}

package com.shejiaomao.weibo.service.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cattong.commons.LibException;
import com.cattong.commons.LibResultCode;
import com.cattong.commons.util.StringUtil;
import com.shejiaomao.common.ImageUtil;
import com.shejiaomao.common.NetType;
import com.shejiaomao.common.ResourceBook;
import com.shejiaomao.weibo.SheJiaoMaoApplication;
import com.shejiaomao.weibo.activity.MicroBlogActivity;
import com.shejiaomao.weibo.common.CacheManager;
import com.shejiaomao.weibo.common.Constants;
import com.shejiaomao.weibo.common.GlobalVars;
import com.shejiaomao.weibo.service.adapter.StatusUtil;
import com.shejiaomao.weibo.service.cache.ImageCache;
import com.shejiaomao.weibo.service.cache.wrap.CachedImage;
import com.shejiaomao.weibo.service.cache.wrap.CachedImageKey;

/*
 * 要确保进度条在imageView 后面
 */
public class ImageLoad4ThumbnailTask extends AsyncTask<com.cattong.entity.Status, Void, CachedImage> {
	private static final String TAG = "ImageLoad4ThumbnailTask";
	private static final String NET_EASE_TAG  = "_thumbnail";
	private ImageCache imageCache;

	private ImageView imageView;
	private TextView tvImageInfo;
	private CachedImageKey key;
	private String url;
	private boolean isHit = false;
	private Bitmap bitmap;
	private CachedImage wrap = null;

	private ProgressBar pBar;
	private String orignUrl;
	private String resultMsg;
	public ImageLoad4ThumbnailTask(ImageView imageView, String url) {
	    this.imageView = imageView;
	    this.orignUrl = url;
	    if (isNetEase(url)) {
	    	this.url = url + NET_EASE_TAG;
	    } else {
	    	this.url = url;
	    }
	    Context context = imageView.getContext();
	    ViewGroup viewGroup = (ViewGroup)imageView.getParent();
	    if (context instanceof MicroBlogActivity) {
		    this.pBar = (ProgressBar)viewGroup.getChildAt(1);
		    this.tvImageInfo = (TextView)((ViewGroup)viewGroup.getParent()).getChildAt(1);
	    } else {
	    	View view = viewGroup.getChildAt(1);
	    	if (view instanceof TextView) {
	    	    this.tvImageInfo = (TextView)viewGroup.getChildAt(1);
	    	}
	    }
	    
		init();
	}
	
	private void init() {
	    imageCache = (ImageCache)CacheManager.getInstance().getCache(ImageCache.class.getName());
        //if (Constants.DEBUG) imageCache.stat();
	    key = new CachedImageKey(url, CachedImageKey.IMAGE_THUMBNAIL);
	    if (url != null
	    	&& (wrap = imageCache.get(key)) != null) {
		    bitmap = wrap.getWrap();
			isHit = true;
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        if (tvImageInfo != null) {
        	tvImageInfo.setVisibility(View.GONE);
        	tvImageInfo.setText("");
        }
		if (isHit && imageView != null) {
			cancel(true);
			onPostExecute(wrap);
			return;
		}
		if (GlobalVars.NET_TYPE == NetType.NONE) {
			cancel(true);
			resultMsg = ResourceBook.getResultCodeValue(LibResultCode.NET_UNCONNECTED, imageView.getContext());
			onPostExecute(null);
		}
	}

	@Override
	protected CachedImage doInBackground(com.cattong.entity.Status... params) {
		if (imageView == null || url == null) {
			return wrap;
		}
		String bigImageUrl = null;
		if (params.length == 1) {
			com.cattong.entity.Status status = params[0];
			bigImageUrl = StatusUtil.getBigImageUrl(status);
		}

		if(Constants.DEBUG) Log.d(TAG, "Get thumbnail image from remote!");
		try {
			byte[] imageData = ImageUtil.getByteArrayByUrl(orignUrl);
			bitmap = ImageUtil.decodeByteArray(imageData);

			if (bitmap == null) {
				return null;
			}
			wrap = new CachedImage(bitmap);

			int maxWidth = 120 * SheJiaoMaoApplication.getDensityDpi() / DisplayMetrics.DENSITY_DEFAULT;
			if (isNetEase(orignUrl)	
				&& (bitmap.getWidth() > maxWidth 
				|| bitmap.getHeight() > maxWidth)) {
				CachedImage midImgWrap = new CachedImage(bitmap);
				CachedImageKey midImgInfo = new CachedImageKey(orignUrl, CachedImageKey.IMAGE_MIDDLE);
				//直接写入文件，不使用bitmap的压缩写入
				ImageCache.write(midImgInfo, imageData);
				midImgWrap.setLocalCached(true);
				imageCache.put(midImgInfo, midImgWrap);

				Bitmap thumbnailMap = ImageUtil.scaleBitmapTo(bitmap, maxWidth);
				//销毁网易的中图
				if (!(thumbnailMap == bitmap || bitmap.isRecycled())) {
					bitmap.recycle();
				}
				bitmap = thumbnailMap;

				wrap = new CachedImage(bitmap);
			} else {
				//直接写入文件，不使用bitmap的压缩写入
				ImageCache.write(key, imageData);
				wrap.setLocalCached(true);
			}

			/**加入cache中**/
			imageCache.put(key, wrap);
			
		} catch (LibException e) {
			if(Constants.DEBUG) Log.e(TAG, e.getMessage(), e);
			resultMsg = ResourceBook.getResultCodeValue(e.getErrorCode(), imageView.getContext());
		}

		return wrap;
	}

	@Override
	protected void onPostExecute(CachedImage result) {
		super.onPostExecute(result);

		if (result != null) {
			imageView.setVisibility(View.VISIBLE);
			imageView.setImageBitmap(bitmap);
			Animation alphaAnimation = imageView.getAnimation();
			if (alphaAnimation != null) {
				alphaAnimation.reset();
			} else {
				alphaAnimation = AnimationUtils.loadAnimation(imageView.getContext(), android.R.anim.fade_in);
			}
			imageView.setAnimation(alphaAnimation);
			if (Constants.DEBUG) Log.v(TAG, "update imageview");

		} else {
			imageView.setVisibility(View.GONE);
			if (Constants.DEBUG && resultMsg != null) {
				Toast.makeText(imageView.getContext(),
					resultMsg, Toast.LENGTH_SHORT).show();
			}
		}

		if (pBar != null) {
			imageView.setVisibility(View.VISIBLE);
		    pBar.setVisibility(View.GONE);
		}
	}

	private boolean isNetEase(String url) {
		if (StringUtil.isEmpty(url)) {
			return false;
		}

		return url.indexOf(Constants.NET_EASE_IMAGE_URL_PREFIX) != -1;
	}
}

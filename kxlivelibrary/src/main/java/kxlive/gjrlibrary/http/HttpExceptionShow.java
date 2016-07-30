package kxlive.gjrlibrary.http;

import android.content.Context;
import android.widget.Toast;

/**
 * 关于网络请求异常的通用提示约定
 * 
 * @author gjr
 * 
 */
public class HttpExceptionShow {

	public static String showInfo(int exceptionType) {
		switch (exceptionType) {
		case 0:
			return "未知异常，请联系开发人员!";
		case 1:
			return "服务器无响应，请重试!";
		case 2:
			return "服务器异常，请联系开发商!";
		case 3:
			return "建立连接异常，检查本地网络或确认地址是否有效!";
		default:
			break;
		}
		return null;
	};

	public static void showInfo(Context mcontext, int exceptionType) {
		switch (exceptionType) {
		case 0:
			Toast.makeText(mcontext, "未知异常，请联系开发人员!", Toast.LENGTH_SHORT)
					.show();
			break;
		case 1:
			Toast.makeText(mcontext, "服务器无响应，请重试!", Toast.LENGTH_SHORT).show();
			break;
		case 2:
			Toast.makeText(mcontext, "服务器异常，请联系开发商!", Toast.LENGTH_SHORT)
					.show();
			break;
		case 3:
			Toast.makeText(mcontext, "建立连接异常，检查本地网络或确认地址是否有效!", Toast.LENGTH_SHORT)
					.show();
			break;
		default:
			break;
		}
	};
}

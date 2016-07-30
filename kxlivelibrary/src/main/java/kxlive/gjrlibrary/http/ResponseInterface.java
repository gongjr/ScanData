package kxlive.gjrlibrary.http;

public interface ResponseInterface {

    /**
     * 接收到服务器返回消息，操作成功
     *
     * @param result
     */
    void onSuccess(String result);

    /**
     * 接收到服务器返回消息，操作失败
     *
     * @result
     */
    void onFailure(String result);

    /**
     * 没有收到服务器返回消息，出现异常
     *
     * @result
     */
    void onException(int exceptionType);

    /**
     * 无论成功与否，最终都需要有所响应，保持良好交互
     */
    void onFinish();

}

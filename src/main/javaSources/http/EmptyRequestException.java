package http;

public class EmptyRequestException extends Exception {
	private static final long serialVersionUID = 1L;//版本号

	public EmptyRequestException() {
		super();
		// TODO 自动生成的构造函数存根
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EmptyRequestException(String message, Throwable cause, boolean enableSuppression,
								 boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO 自动生成的构造函数存根
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EmptyRequestException(String message, Throwable cause) {
		super(message, cause);
		// TODO 自动生成的构造函数存根
	}

	/**
	 * @param message
	 */
	public EmptyRequestException(String message) {
		super(message);
		// TODO 自动生成的构造函数存根
	}

	/**
	 * @param cause
	 */
	public EmptyRequestException(Throwable cause) {
		super(cause);
		// TODO 自动生成的构造函数存根
	}


}

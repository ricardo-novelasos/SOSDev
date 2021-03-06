package com.github.sarxos.webcam;

public abstract class WebcamTask {

	private boolean sync = true;
	private WebcamProcessor processor = null;
	private WebcamDevice device = null;
	private Throwable throwable = null;

	public WebcamTask(boolean threadSafe, WebcamDevice device) {
		this.sync = !threadSafe;
		this.device = device;
		this.processor = WebcamProcessor.getInstance();
	}

	public WebcamTask(WebcamDriver driver, WebcamDevice device) {
		this(driver.isThreadSafe(), device);
	}

	public WebcamTask(WebcamDevice device) {
		this(false, device);
	}

	public WebcamDevice getDevice() {
		return device;
	}

	/**
	 * Process task by processor thread.
	 * 
	 * @throws InterruptedException when thread has been interrupted
	 */
	public void process() throws InterruptedException {
		if(sync) {
			if(processor == null) {
				throw new RuntimeException("Driver should be synchronized, but processor is null");
			}
			processor.process(this);
		} else {
			handle();
		}
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable t) {
		this.throwable = t;
	}

	protected abstract void handle();
}

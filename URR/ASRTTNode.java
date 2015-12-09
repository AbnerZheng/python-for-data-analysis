package mergecf.entity;


public class ASRTTNode implements Comparable<ASRTTNode>{
	private float rtt;
	private int userId;
	private String service;
	private String ip;
	private int regionId;



	public ASRTTNode(float rtt, int userId, String service, String ip, int regionId) {
		super();
		this.rtt = rtt;
		this.userId = userId;
		this.service = service;
		this.ip = ip;
		this.regionId = regionId;
	}

	

	public int getRegionId() {
		return regionId;
	}



	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}



	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public float getRtt() {
		return rtt;
	}

	public void setRtt(float rtt) {
		this.rtt = rtt;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!this.getClass().equals(obj.getClass()))
			return false;
		ASRTTNode other = (ASRTTNode) obj;
		if (this.rtt == other.rtt && this.ip.equals(other.ip)
				&& this.service.equals(other.service) && this.userId == other.userId
				&& this.regionId==other.regionId)
			return true;
		return false;
	}

	public int compareTo(ASRTTNode o) {
		if (this.rtt > o.rtt)
		return -1;
	else if (this.rtt == o.rtt) {
		return 0;
	}
	return 1;
	}

	




}

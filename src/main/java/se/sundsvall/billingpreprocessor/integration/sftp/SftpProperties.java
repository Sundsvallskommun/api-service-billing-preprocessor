package se.sundsvall.billingpreprocessor.integration.sftp;

import java.util.Objects;

public final class SftpProperties {
	private String host;
	private int port;
	private String user;
	private String password;
	private String remoteDir;
	private boolean allowUnknownKeys;
	private String knownHosts;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemoteDir() {
		return remoteDir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	public boolean isAllowUnknownKeys() {
		return allowUnknownKeys;
	}

	public void setAllowUnknownKeys(boolean allowUnknownKeys) {
		this.allowUnknownKeys = allowUnknownKeys;
	}

	public String getKnownHosts() {
		return knownHosts;
	}

	public void setKnownHosts(String knownHosts) {
		this.knownHosts = knownHosts;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (SftpProperties) obj;
		return Objects.equals(this.host, that.host) &&
			this.port == that.port &&
			Objects.equals(this.user, that.user) &&
			Objects.equals(this.password, that.password) &&
			Objects.equals(this.remoteDir, that.remoteDir) &&
			this.allowUnknownKeys == that.allowUnknownKeys &&
			Objects.equals(this.knownHosts, that.knownHosts);
	}

	@Override
	public int hashCode() {
		return Objects.hash(host, port, user, password, remoteDir, allowUnknownKeys, knownHosts);
	}

	@Override
	public String toString() {
		return "SftpProperties[" +
			"host=" + host + ", " +
			"port=" + port + ", " +
			"user=" + user + ", " +
			"password=" + password + ", " +
			"remoteDir=" + remoteDir + ", " +
			"allowUnknownKeys=" + allowUnknownKeys + ", " +
			"knownHosts=" + knownHosts + ']';
	}


}

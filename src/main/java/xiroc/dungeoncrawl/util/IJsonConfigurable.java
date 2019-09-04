package xiroc.dungeoncrawl.util;

import java.io.File;
import java.util.HashMap;

import com.google.gson.JsonObject;

public interface IJsonConfigurable {

	public File getFile();

	public void load(JsonObject object, File file);

	public JsonObject create(JsonObject object);

	public HashMap<String, Object> getDefaults();
	
	public String[] getKeys();

}

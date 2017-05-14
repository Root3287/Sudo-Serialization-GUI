package site.root3287.serializationgui;
import java.io.File;

import site.root3287.sudo.serialization.container.SerializationDatabase;

public class SerializationPharser {
	public SerializationDatabase database;
	public SerializationPharser(File file){
		database = SerializationDatabase.deserializeFile(file.getPath());
	}
	public SerializationPharser(){
		database = new SerializationDatabase("null");
	}
}

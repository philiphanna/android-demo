package uk.ac.qub.eeecs.demos.unused;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import uk.ac.qub.eeecs.demos.R;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ExternalStorageTestFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.external_storage_test_fragment, container, false);
		
		TextView outputTextView = (TextView)view.findViewById(R.id.external_storage_test_textview);	
		
		String state = Environment.getExternalStorageState();		
		if( !state.equals(Environment.MEDIA_MOUNTED)) {
			outputTextView.setText("No external storage available.");
		} else {
			File directory = Environment.getExternalStorageDirectory();
			File textFile = new File(directory.getAbsolutePath() + 
					File.separator + "tempOutput.txt");
			try {
				writeTextFile(textFile, "Some temporary output data");
				String text = readTextFile(textFile);
				outputTextView.setText(text);
				if (!textFile.delete()) {
					outputTextView.setText("Could not delete file.");
				}
			} catch (IOException e) {
				outputTextView.setText("IOException thrown.");
			}
		}
		
		return view;
	}	
	
	private void writeTextFile(File file, String text) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(text);
		writer.close();
	}
	
	private String readTextFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
			builder.append("\n");
		}
		reader.close();
		return builder.toString();		
	}
}

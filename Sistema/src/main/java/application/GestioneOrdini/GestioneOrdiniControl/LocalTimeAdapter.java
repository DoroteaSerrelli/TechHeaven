/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package application.GestioneOrdini.GestioneOrdiniControl;

/**
 *
 * @author raffa
 */
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalTime;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

    @Override
    public void write(JsonWriter out, LocalTime value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public LocalTime read(JsonReader in) throws IOException {
        return LocalTime.parse(in.nextString());
    }
}

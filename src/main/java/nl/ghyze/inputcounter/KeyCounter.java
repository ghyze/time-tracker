package nl.ghyze.inputcounter;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import java.util.logging.Level;

public class KeyCounter implements NativeKeyListener, NativeMouseListener {

    private int keysPressed = 0;
    private int clicks = 0;

    public KeyCounter(){
        init();
    }

    public static void main(String... args){
        new KeyCounter();
        System.out.println("Native key listener registered");
    }

    private void init() {

        // Get the logger for "org.jnativehook" and set the level to warning.
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);

        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);


        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e){
            System.out.println("Error: "+e.getMessage());
        }
        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        keysPressed++;
//        System.out.println("Key pressed: "+getKeyText(nativeKeyEvent));
//        System.out.println(nativeKeyEvent.paramString());
//        System.out.println("Modifiers: "+nativeKeyEvent.getModifiers());
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
    }

    public int getKeys(){
        int localKeysPressed = keysPressed;
        resetKeys();
        return localKeysPressed;
    }

    public int getClicks(){
        int localClicks = clicks;
        clicks = 0;
        return localClicks;
    }

    private void resetKeys(){
        keysPressed = 0;
    }

    private String getKeyText(NativeKeyEvent nativeKeyEvent){
        return NativeKeyEvent.getKeyText(nativeKeyEvent.getKeyCode());
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        clicks++;
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {

    }
}

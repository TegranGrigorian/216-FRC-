package frc.robot;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import frc.robot.subsystems.IntakeSubsystem;

public class Leds {
    private final AddressableLED leds;
    private final AddressableLEDBuffer ledBuffer;
    public Leds(int port, int buffer) {
        leds = new AddressableLED(port);
        ledBuffer = new AddressableLEDBuffer(buffer);
        leds.setLength(buffer);
    }
    public void shutOffLeds() {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setHSV(i, 0, 0, 0);
        }
        leds.setData(ledBuffer); //send data to the leds
    }
    public void setRainbow(int speed, int brightness, int saturation) {
        //were gonna make 'defuauls' be 5, 128 255
        int rainbowFirstPixelHue = 0; // Track the starting hue for animation
    
        // Run a loop for updating the LEDs (you can call this periodically in a scheduler)
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            // Calculate hue for each LED
            int hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;
            ledBuffer.setHSV(i, hue, saturation, brightness);
        }
        leds.setData(ledBuffer);
    
        // Adjust the starting hue for the next frame based on speed
        rainbowFirstPixelHue += speed;
        rainbowFirstPixelHue %= 180; // Wrap around hue after 180
    }
    public void solidEffect(int hue, int saturation,int brightness) {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setHSV(i, hue, saturation, brightness);
        }
    }
    public void breatingEffect(int hue, int saturation, int brightness, double period, double time) {
        // Calculate the brightness based on a sine wave
        double radians = (2 * Math.PI * time) / period; // did you guys know that hue is a cirlce, I didnt until now, its pretty cool!
        int bright = (int) ((Math.sin(radians) + 1) / 2 * brightness); // 

        // Set the color with the calculated brightness
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setHSV(i, hue, saturation, bright);
        }
        leds.setData(ledBuffer); // Update the LED strip with the new data
    }
    public void setChase(int hue, int saturation, int brightness, double period, double elapsedTime) { //ring around the rosie irl
        int chasePosition = (int) ((elapsedTime / period * ledBuffer.getLength()) % ledBuffer.getLength());
        
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            if (i == chasePosition) {
                ledBuffer.setHSV(i, hue, saturation, brightness);
            } else {
                ledBuffer.setHSV(i, hue, saturation, 0); 
            }
        }
        leds.setData(ledBuffer);
    }
    public void setWave(int hue, int saturation, int brightness, double period, double elapsedTime) {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            double wavePosition = (i + elapsedTime * ledBuffer.getLength() / period) % ledBuffer.getLength();
            int adjustedBrightness = (int) ((Math.sin((wavePosition / ledBuffer.getLength()) * 2 * Math.PI) + 1) / 2 * brightness);
            ledBuffer.setHSV(i, hue, saturation, adjustedBrightness);
        }
        leds.setData(ledBuffer);
    }
    public void setBlink(int hue, int saturation, int brightness, double interval, double elapsedTime) {
        boolean isOn = ((int) (elapsedTime / interval) % 2) == 0; // Toggle every interval
        int finalBrightness = isOn ? brightness : 0;
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            ledBuffer.setHSV(i, hue, saturation, finalBrightness);
        }
        leds.setData(ledBuffer);
    }
    public void setColorGradient(int baseHue, int saturation, int brightness, double period, double elapsedTime) {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            int hue = (baseHue + (i * 180 / ledBuffer.getLength()) + (int) (elapsedTime * 180 / period)) % 180;
            ledBuffer.setHSV(i, hue, saturation, brightness);
        }
        leds.setData(ledBuffer);
    }
    public void setComet(int hue, int saturation, int brightness, double period, double elapsedTime) {
        int cometHead = (int) ((elapsedTime / period * ledBuffer.getLength()) % ledBuffer.getLength());
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            int distanceFromHead = Math.abs(cometHead - i);
            int fadeBrightness = Math.max(0, brightness - (distanceFromHead * (brightness / 5))); // Tail fades over 5 LEDs
            ledBuffer.setHSV(i, hue, saturation, fadeBrightness);
        }
        leds.setData(ledBuffer);
    }
    public void setRandomTwinkle(int hue, int saturation, int brightness, double period, double elapsedTime) {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            boolean isOn = Math.random() < 0.05; // Randomly choose if the LED is on
            int finalBrightness = isOn ? brightness : 0;
            ledBuffer.setHSV(i, hue, saturation, finalBrightness);
        }
        leds.setData(ledBuffer);
    }
    public void hollowPurple(int blueHue, int redHue, int purpleHue, int saturation, int maxBrightness, double period, double elapsedTime) {
        int length = ledBuffer.getLength();
        int midpoint = length / 2;
    
        double cycleProgress = (elapsedTime % period) / period; // 0 to 1 within the period
        int currentPosition = (int) (cycleProgress * midpoint);
    
        for (int i = 0; i < length; i++) {
            if (i < midpoint - currentPosition) {
                // Blue side moving toward the center
                ledBuffer.setHSV(i, blueHue, saturation, maxBrightness);
            } else if (i > midpoint + currentPosition) {
                // Red side moving toward the center
                ledBuffer.setHSV(i, redHue, saturation, maxBrightness);
            } else {
                // Collision zone: Create purple explosion
                double explosionProgress = Math.max(0, cycleProgress - 0.8) / 0.2; // Explosion in the last 20% of the cycle
                int brightness = (int) (explosionProgress * maxBrightness); // Gradual brightness increase
                ledBuffer.setHSV(i, purpleHue, saturation, brightness);
            }
        }
        leds.setData(ledBuffer);
    }
    public void setLedsIfIntakeDetects() { //example function of what you could do if you want the leds to light up if the laser break detects something
        IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
        if (intakeSubsystem.isLaserBreakTriggered()) {
            setBlink(120, 100, 100, 10, 1000); //do some cool light stuff.
        }
    }
}
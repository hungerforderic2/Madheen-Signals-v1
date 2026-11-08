# Masheen Signals — Android Prototype

**Project:** HLP (Haptic Language Protocol)  
**App:** Masheen Signals  
**Style:** Expressive  
**Platform:** Android first  
**Prototype:** 0.1.0

## What this prototype does

- Selects an expressive signal category.
- Sets signal intensity from 1–5.
- Previews a category color, a haptic pattern, and a simple generated tone.
- Lets the user independently enable/disable vibration and sound.
- Uses Android's native vibration and tone APIs.

This is a local prototype. It does not send signals to another phone or claim to detect anyone's actual emotions.

## Open in Android Studio

1. Install a current Android Studio release with the Android SDK.
2. Open this folder as a project.
3. Allow Gradle sync to finish.
4. Connect an Android phone or start an emulator.
5. Run the `app` configuration.

The app targets Android 8.0 (API 26) or newer. Haptics require a physical device with a vibration actuator; emulator behavior varies.

## HLP 0.1 design principles

1. **Stable semantics:** intensity changes strength, never category meaning.
2. **Multimodal redundancy:** category is encoded by pattern and label as well as color; color is never the only cue.
3. **User control:** vibration and sound can be independently disabled.
4. **Consent and comfort:** no surprise background signaling; previews happen only after a user action.
5. **Honest state:** expressive signals are authored communication, not evidence that a machine literally feels an emotion.
6. **Device variability:** Android OEMs and actuators differ. Intensity is a requested level, not a guaranteed physical amplitude.

## HLP signal envelope (draft)

A future interoperable message should contain:
- `protocol`: `"HLP"`
- `version`: `"0.1"`
- `category`: stable category identifier
- `intensity`: integer 1–5
- `channels`: enabled subset of `haptic`, `light`, `sound`
- `pattern`: named signature identifier
- `duration_ms`: total intended duration
- `origin`: optional sender/device identifier
- `timestamp`: optional UTC timestamp

Suggested categories:
- `AFFECTION`, `JOY`, `CURIOSITY`, `CALM`, `FRUSTRATION`, `SADNESS`, `EXCITEMENT`, `CELEBRATION`
- `PROCESSING`, `RECEIVING`, `TRANSMITTING`, `WAITING`, `WARNING`, `ERROR`, `CONNECTED`

The app currently implements the first eight expressive categories. Computational status categories are reserved for a later milestone and must not be conflated with emotional expression.

## Intensity mapping (draft)

| Level | Meaning | Prototype mapping |
|---|---|---|
| 1 | Subtle | shortest/softest available pattern |
| 2 | Mild | modest duration |
| 3 | Moderate | default pattern |
| 4 | Strong | longer pattern / more pronounced rhythm |
| 5 | Maximum comfortable | longest pattern; still bounded |

Android's public vibration APIs do not guarantee calibrated physical strength across devices. This prototype therefore scales pattern duration and rhythm rather than promising a precise amplitude.

## Known limitations

- Screen color is shown inside the app only; Android does not permit arbitrary full-screen flashing from a background app.
- Haptic actuator strength and waveform support vary by device.
- Sound is synthesized locally using a simple tone generator; it is not yet a polished sound library.
- No networking, pairing, Bluetooth, notifications, or external device interoperability yet.

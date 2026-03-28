# Story: AI Voice Assistant (Hands-Free Coaching)

## 🎯 Goal
Allow the runner to interact with their AI Coach through voice commands, making the experience hands-free and immersive during high-intensity sessions.

## 👤 User Story
`As a Runner, I want to ask 'What's my target heart rate for this interval?' while running so that I can stay in the zone without reaching for my phone.`

## 🛠️ Acceptance Criteria
- [ ] Logic: Connect the "Live Session Guide" to the **Text-to-Speech (TTS)** and **Speech-to-Text (STT)** APIs.
- [ ] Commands: Support "Check Pace", "Check Heart Rate", "Next Interval", and "Finish Run".
- [ ] Context: The AI must understand the context of the current `program_session`.
- [ ] Safety: Voice interaction must be low-distraction (short, clear prompts).

## 🚀 Powerful Addition: "RAG Coaching Knowledge"
Feed the AI Coach professional training books and scientific journals. If the user asks "Why do I feel pain in my shins?", the AI can provide a medically-backed "Caution" note and suggest slowing down or ice therapy based on its internal knowledge base.

## 💡 Technical Strategy
1. Use the browser-native `Web Speech API` for low-latency basic commands.
2. For "Complex Questions," send the audio stream (compressed) to an LLM-backend.
3. Optimize for wind-noise reduction on the STT input.

# Story: Real-time Audio Kudos (Vibe Cheers)

## 🎯 Goal
Bring the community atmosphere to every solo run by allowing runners to hear their friends' support in real-time through their headphones.

## 👤 User Story
`As a Runner, I want to hear a 'Go on!' cheer through my headphones when a friend likes my active run on the live feed.`

## 🛠️ Acceptance Criteria
- [ ] Tracking: When a user starts an "Active Session", their state is set to `RUNNING` in the database.
- [ ] Notification Socket: Use **WebSockets (STOMP)** to send a real-time event when a friend clicks the "Kudos" button during the active session.
- [ ] Audio: The mobile app plays a short, high-quality audio clip (crowd cheer, whistle, or voice shoutout).
- [ ] Feedback: Show a notification on the runner's phone: "John Doe just cheered for you!".

## 🚀 Powerful Addition: "The Personalized Shoutout"
Allow the cheering friend to record a short 3-second audio clip that gets streamed directly to the runner's headphones. This creates an immersive, connected experience.

## 💡 Technical Strategy
1. Backend: Implement a `KudosMessage` payload with `sender_id`, `receiver_id`, and `audio_url`.
2. Frontend: Use the `Web Audio API` or `HTML5 Audio` to play the files in the background.
3. Security: Allow users to "Mute Cheers" in their Privacy Settings.

# Story: Digital Collectibles (3D Finisher Medals)

## 🎯 Goal
Celebrate athletic achievements with high-fidelity, tradable, or displayable digital assets, moving beyond flat badges.

## 👤 User Story
`As a Runner, I want a 3D model of my 'Marathon Finisher' medal after I complete my 16-week program so that I can show it off on my profile.`

## 🛠️ Acceptance Criteria
- [ ] Display: A "Trophy Cabinet" in the user profile using **Three.js** to render 3D medals.
- [ ] Animation: Medals should spin and reflect light based on the device's movement (gyroscope).
- [ ] Integration: Automatically unlock the 3D model when the `user_program_progress` state is set to `COMPLETED`.

## 🚀 Powerful Addition: "The AR Projection"
Allow the user to view their medal in **Augmented Reality (AR)** using their phone's camera, "placing" it in their real-world environment.

## 💡 Technical Strategy
1. Store medal models as `.glb` or `.gltf` files.
2. Optimize for web-performance using `DRACO` compression.
3. Use a "Procedural Texture" that changes the medal color (Gold, Silver, Bronze) based on the runner's percentile in the leaderboard.

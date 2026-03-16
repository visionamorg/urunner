package com.runhub.ai.service;

import com.runhub.ai.dto.AiResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AiService {

    public AiResponse ask(String question) {
        if (question == null || question.isBlank()) {
            return AiResponse.builder()
                    .answer("Please ask me a running question!")
                    .tips(List.of("What is your running goal?", "How often do you run?"))
                    .build();
        }

        String q = question.toLowerCase(Locale.ROOT);

        if (q.contains("beginner") || q.contains("start") || q.contains("new")) {
            return AiResponse.builder()
                    .answer("Welcome to running! The most important thing for beginners is to start slow and build consistency. Follow the 10% rule - never increase your weekly mileage by more than 10% per week. Focus on completing your runs, not on speed.")
                    .tips(List.of(
                            "Start with a run/walk program (e.g., Couch to 5K)",
                            "Invest in proper running shoes from a specialty store",
                            "Run at a conversational pace where you can speak in full sentences",
                            "Rest days are as important as running days",
                            "Listen to your body - it's okay to take an extra rest day"
                    ))
                    .build();
        }

        if (q.contains("marathon") || q.contains("42")) {
            return AiResponse.builder()
                    .answer("Marathon training requires a solid 16-20 week preparation. You should have a base of at least 30-40km per week before starting marathon training. The key workouts are the weekly long run, tempo runs, and recovery runs.")
                    .tips(List.of(
                            "Peak weekly mileage should reach 60-80km for most runners",
                            "The long run should never exceed 32-35km in training",
                            "Practice your race nutrition on long runs",
                            "Taper for 3 weeks before race day",
                            "Run the first half of the marathon conservatively"
                    ))
                    .build();
        }

        if (q.contains("pace") || q.contains("speed") || q.contains("fast")) {
            return AiResponse.builder()
                    .answer("Improving your running pace takes time and structured training. The key is to run most of your miles at easy pace (80%), and only 20% at harder intensities. Speed work like intervals and tempo runs will improve your race pace significantly.")
                    .tips(List.of(
                            "80% of your runs should be at easy conversational pace",
                            "Add one weekly tempo run at 'comfortably hard' effort",
                            "Try interval training: 6x800m at 5K pace with recovery jogs",
                            "Strides after easy runs improve running economy",
                            "Hill training builds speed and strength simultaneously"
                    ))
                    .build();
        }

        if (q.contains("nutrition") || q.contains("food") || q.contains("eat") || q.contains("fuel")) {
            return AiResponse.builder()
                    .answer("Nutrition is the fourth discipline of running. For runs under 60-75 minutes, you generally don't need to fuel during the run. For longer runs, aim to take 30-60g of carbohydrates per hour. Hydration is critical - drink to thirst.")
                    .tips(List.of(
                            "Eat a carb-rich meal 2-3 hours before long runs",
                            "Practice race nutrition in training, never try new foods on race day",
                            "For runs over 75 minutes, take gels or energy chews every 45 minutes",
                            "Recover with a mix of protein and carbs within 30 minutes of hard runs",
                            "Stay well hydrated throughout the day, not just during runs"
                    ))
                    .build();
        }

        if (q.contains("injury") || q.contains("pain") || q.contains("hurt") || q.contains("prevent")) {
            return AiResponse.builder()
                    .answer("Most running injuries are overuse injuries caused by doing too much too soon. The key to staying injury-free is gradual progression, adequate recovery, and listening to your body. Pain is your body's signal - don't ignore it.")
                    .tips(List.of(
                            "Follow the 10% rule for weekly mileage increases",
                            "Include at least one full rest day per week",
                            "Strength training (especially glutes and hips) prevents many injuries",
                            "Replace running shoes every 500-800km",
                            "If something hurts, stop running and assess - 2 easy days beats 2 weeks off"
                    ))
                    .build();
        }

        if (q.contains("half") || q.contains("21")) {
            return AiResponse.builder()
                    .answer("The half marathon is a fantastic race distance - long enough to be a real challenge, short enough to recover quickly. A 10-12 week training plan is typical. Aim to run 4-5 times per week with one long run reaching 18-20km before race day.")
                    .tips(List.of(
                            "Long runs should top out at 18-20km, 2-3 weeks before race day",
                            "Include a weekly tempo run at your goal half marathon pace",
                            "Taper for 1-2 weeks before race day",
                            "Aim to run the race at even splits - start conservatively",
                            "Practice fueling during your long runs if taking gels on race day"
                    ))
                    .build();
        }

        if (q.contains("recover") || q.contains("rest")) {
            return AiResponse.builder()
                    .answer("Recovery is where the real training adaptations happen. Your body gets stronger during rest, not during the hard workouts. Prioritize sleep (7-9 hours), nutrition, and easy running between hard efforts.")
                    .tips(List.of(
                            "Sleep is the most powerful recovery tool - prioritize 7-9 hours",
                            "Easy/recovery runs should feel genuinely easy (heart rate under 140)",
                            "Foam rolling and stretching help with muscle soreness",
                            "Compression socks or elevation can reduce swelling after long runs",
                            "Active recovery (swimming, cycling) is better than complete rest"
                    ))
                    .build();
        }

        // Default response
        return AiResponse.builder()
                .answer("Running is one of the most rewarding sports you can pursue! Whether you're training for your first 5K or your tenth marathon, consistency is the key to improvement. Keep showing up, run smart, and enjoy the journey.")
                .tips(List.of(
                        "Consistency beats intensity - run regularly even if shorter distances",
                        "Most of your runs (80%) should be at easy conversational pace",
                        "Follow the 10% rule when increasing weekly mileage",
                        "Track your runs to see your progress over time",
                        "Join a running community for motivation and support",
                        "Set specific, measurable goals to stay motivated"
                ))
                .build();
    }
}

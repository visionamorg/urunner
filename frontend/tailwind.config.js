/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  theme: {
    extend: {
      fontFamily: { sans: ['Inter', 'system-ui', 'sans-serif'] },
      colors: {
        brand: {
          bg: '#050a18',
          card: '#0c1428',
          surface: '#111d35',
          border: '#1c2d45',
        }
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
      },
      keyframes: {
        fadeIn: { '0%': { opacity: '0' }, '100%': { opacity: '1' } },
        slideUp: { '0%': { opacity: '0', transform: 'translateY(10px)' }, '100%': { opacity: '1', transform: 'translateY(0)' } },
      }
    }
  },
  safelist: [
    'bg-orange-500/10', 'bg-orange-500/20', 'border-orange-500/30',
    'bg-green-500/10', 'border-green-500/20',
    'bg-yellow-500/5', 'bg-slate-500/5', 'bg-orange-900/5',
    'text-green-400', 'text-orange-400',
  ],
  plugins: []
}

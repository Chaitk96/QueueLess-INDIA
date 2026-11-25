import React from 'react';
import beachHuman from './assets/queue-less-beach-human.png';

export default function BeachIllustration(){return <div className="beach-real-hero" role="img" aria-label="A relaxed customer uses QueueLess to book an appointment from a beach chair"><span className="beach-sun-sticker">✦</span><img className="beach-photo-human" src={beachHuman} alt="A relaxed young man checking his appointment on his phone from a beach lounger"/><div className="beach-booking-pill"><span className="booking-pill-check">✓</span><span><b>Queue handled</b><small>Beach day still yours</small></span></div></div>}

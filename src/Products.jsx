import React from 'react';
import { ArrowUpRight, X } from 'lucide-react';
import inStoreImage from './assets/products/in-store-payments.jpg';
import onlineImage from './assets/products/online-payments.jpg';
import prepaidImage from './assets/products/prepaid.jpg';
import infrastructureImage from './assets/products/financial-infrastructure.jpg';
import creditImage from './assets/products/credit-processing.jpg';

const products=[
  {title:'In-store Payments',description:'PoS, UPI, EMI, Growth Suite & more — everything you need to run and grow your retail business, every day.',image:inStoreImage},
  {title:'Online Payments',description:'Payment gateway, payouts, affordability for online businesses',image:onlineImage},
  {title:'Prepaid',description:'Gift cards, network prepaid cards, engagement & loyalty programs tailored for businesses.',image:prepaidImage},
  {title:'Financial Infrastructure',description:'Lending, payments and ID verification within your platform',image:infrastructureImage},
  {title:'Credit Processing',description:'Unified platform for seamless card issuance & processing',image:creditImage},
];

export default function ProductsModal({close}){
  return <div className="products-scrim" onMouseDown={e=>{if(e.target===e.currentTarget)close()}}><section className="products-modal" role="dialog" aria-modal="true" aria-labelledby="products-title"><button className="products-close" onClick={close} aria-label="Close products"><X size={20}/></button><div className="products-heading"><span className="section-kicker product-kicker">QueueLess INDIA · BUSINESS SOLUTIONS</span><h2 id="products-title">Let’s get you started with<br/>the right solution</h2></div><div className="products-grid">{products.map(product=><article className="product-card" key={product.title}><div className="product-card-title"><h3>{product.title}</h3><ArrowUpRight size={20}/></div><p>{product.description}</p><img src={product.image} alt={product.title} loading="lazy"/></article>)}</div></section></div>
}

import { Link } from 'react-router-dom'
import { inr } from '../lib/format'

export default function ProductCard({ product }) {
  return (
    <article className="group">
      <Link to={`/product/${product.id}`} className="block">
        <div className="aspect-[4/5] overflow-hidden bg-sand">
          {product.image && (
            <img
              src={product.image}
              alt={product.imageAlt || product.name}
              loading="lazy"
              className="h-full w-full object-cover transition duration-500 group-hover:scale-[1.03]"
            />
          )}
        </div>
        <div className="mt-3 flex items-start justify-between gap-3">
          <div>
            <h3 className="font-display text-lg leading-tight">{product.name}</h3>
            <p className="text-sm text-sage">{product.artisanName}</p>
          </div>
          <p className="text-sm">{inr(product.price)}</p>
        </div>
        <p className="text-xs mt-1 uppercase tracking-wide text-clay">
          {product.soldOut ? 'Sold out' : product.oneOfAKind ? 'One of a kind' : product.technique}
        </p>
      </Link>
    </article>
  )
}

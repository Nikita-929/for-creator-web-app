import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import { useState } from 'react'
import api from '../api/client'
import { inr, productJsonLd } from '../lib/format'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import ProductCard from '../components/ProductCard'
import Seo from '../components/Seo'

export default function ProductPage() {
  const { id } = useParams()
  const { add } = useCart()
  const { user } = useAuth()
  const qc = useQueryClient()
  const [rating, setRating] = useState(5)
  const [comment, setComment] = useState('')
  const [reviewImage, setReviewImage] = useState('')
  const [zoom, setZoom] = useState(0)

  const product = useQuery({ queryKey: ['product', id], queryFn: () => api.get(`/api/products/${id}`).then((r) => r.data) })
  const related = useQuery({ queryKey: ['related', id], queryFn: () => api.get(`/api/products/${id}/related`).then((r) => r.data) })
  const reviews = useQuery({ queryKey: ['reviews', id], queryFn: () => api.get(`/api/reviews/product/${id}`).then((r) => r.data) })

  const reviewMut = useMutation({
    mutationFn: () =>
      api.post(`/api/reviews/product/${id}`, { rating, comment, images: reviewImage ? [reviewImage] : [] }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['reviews', id] })
      setComment('')
    },
  })

  if (product.isLoading) return <p className="p-8">Loading…</p>
  if (product.isError) return <p className="p-8">This piece could not be found.</p>
  const p = product.data
  const img = p.images?.[zoom] || p.images?.[0]

  return (
    <div className="mx-auto max-w-6xl px-4 py-10">
      <Seo title={p.name} description={p.description} jsonLd={productJsonLd(p)} />
      <div className="grid md:grid-cols-2 gap-10">
        <div>
          {img && (
            <img
              src={img}
              alt={p.imageAlts?.[zoom] || p.name}
              className="w-full aspect-square object-cover bg-sand cursor-zoom-in"
            />
          )}
          <div className="flex gap-2 mt-3">
            {p.images?.map((src, i) => (
              <button key={src} type="button" onClick={() => setZoom(i)} aria-label={`View image ${i + 1}`}>
                <img src={src} alt="" className="h-16 w-16 object-cover" />
              </button>
            ))}
          </div>
        </div>
        <div>
          <p className="text-xs uppercase tracking-widest text-clay">{p.categoryType}</p>
          <h1 className="font-display text-4xl mt-2">{p.name}</h1>
          <p className="mt-2 text-xl">{inr(p.price)}</p>
          {p.oneOfAKind && <p className="mt-2 text-sm text-clay">One of a kind — not restocked.</p>}
          {p.soldOut && <p className="mt-2 font-medium">Sold out</p>}
          <p className="mt-6 leading-relaxed">{p.description}</p>
          <dl className="mt-6 text-sm grid grid-cols-2 gap-2">
            <dt className="text-sage">Technique</dt>
            <dd>{p.technique}</dd>
            <dt className="text-sage">Materials</dt>
            <dd>{p.materials?.join(', ')}</dd>
            <dt className="text-sage">Dimensions</dt>
            <dd>{p.dimensions}</dd>
            <dt className="text-sage">Artisan</dt>
            <dd>
              <Link className="underline" to={`/artisans/${p.artisanId}`}>
                {p.artisanName}
              </Link>
            </dd>
          </dl>
          <div className="mt-8 flex gap-3">
            <button
              type="button"
              disabled={p.soldOut}
              onClick={() => add(p)}
              className="bg-ink text-cream px-5 py-3 rounded disabled:opacity-40"
            >
              Add to cart
            </button>
            <Link to="/custom-orders" className="border border-ink px-5 py-3 rounded">
              Enquire / commission
            </Link>
            {user && (
              <button
                type="button"
                className="underline"
                onClick={async () => {
                  await api.post(`/api/wishlist/${p.id}`)
                  qc.invalidateQueries({ queryKey: ['wishlist'] })
                }}
              >
                Wishlist
              </button>
            )}
          </div>
        </div>
      </div>
      <section className="mt-16">
        <h2 className="font-display text-2xl">Reviews</h2>
        <ul className="mt-4 space-y-4">
          {reviews.data?.map((r) => (
            <li key={r.id} className="border-b border-sand pb-3">
              <p className="text-sm">
                {r.userName} — {r.rating}/5
              </p>
              <p>{r.comment}</p>
              {r.images?.[0] && <img src={r.images[0]} alt="" className="mt-2 h-24 object-cover" />}
            </li>
          ))}
        </ul>
        {user ? (
          <form
            className="mt-6 max-w-md space-y-3"
            onSubmit={(e) => {
              e.preventDefault()
              reviewMut.mutate()
            }}
          >
            <label className="block text-sm">
              Rating
              <input type="number" min="1" max="5" value={rating} onChange={(e) => setRating(Number(e.target.value))} className="ml-2 border px-2" />
            </label>
            <textarea required value={comment} onChange={(e) => setComment(e.target.value)} className="w-full border border-sand p-3" rows="3" />
            <label className="block text-sm">
              Optional photo
              <input
                type="file"
                accept="image/*"
                className="mt-1 block"
                onChange={async (e) => {
                  const file = e.target.files?.[0]
                  if (!file) return
                  const body = new FormData()
                  body.append('file', file)
                  const { data } = await api.post('/api/uploads', body)
                  setReviewImage(data.url)
                }}
              />
            </label>
            <button className="bg-clay text-cream px-4 py-2 rounded" type="submit">
              Post review
            </button>
          </form>
        ) : (
          <p className="mt-4 text-sm">
            <Link to="/login" className="underline">
              Sign in
            </Link>{' '}
            to review.
          </p>
        )}
      </section>
      <section className="mt-16">
        <h2 className="font-display text-2xl mb-6">Related</h2>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {related.data?.map((item) => (
            <ProductCard key={item.id} product={item} />
          ))}
        </div>
      </section>
    </div>
  )
}

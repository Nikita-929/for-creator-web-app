import { useQuery } from '@tanstack/react-query'
import { useSearchParams } from 'react-router-dom'
import api from '../api/client'
import ProductCard from '../components/ProductCard'
import Seo from '../components/Seo'

export default function ShopPage() {
  const [params, setParams] = useSearchParams()
  const query = Object.fromEntries(params.entries())

  const products = useQuery({
    queryKey: ['products', query],
    queryFn: () => api.get('/api/products', { params: query }).then((r) => r.data),
  })
  const categories = useQuery({ queryKey: ['categories'], queryFn: () => api.get('/api/categories').then((r) => r.data) })
  const artisans = useQuery({ queryKey: ['artisans'], queryFn: () => api.get('/api/artisans').then((r) => r.data) })

  function set(key, value) {
    const next = new URLSearchParams(params)
    if (!value) next.delete(key)
    else next.set(key, value)
    if (key !== 'page') next.delete('page')
    setParams(next)
  }

  const content = products.data?.content ?? []

  return (
    <div className="mx-auto max-w-6xl px-4 py-10">
      <Seo title="Shop" description="Filter handmade products by category, material, price, technique, and artisan." />
      <h1 className="font-display text-4xl">Shop</h1>
      <div className="mt-8 grid md:grid-cols-[240px_1fr] gap-10">
        <form className="space-y-4 text-sm" onSubmit={(e) => e.preventDefault()} aria-label="Filters">
          <label className="block">
            Search
            <input
              className="mt-1 w-full border border-sand px-3 py-2 bg-paper"
              defaultValue={query.q}
              onBlur={(e) => set('q', e.target.value)}
            />
          </label>
          <fieldset>
            <legend className="font-medium">Line</legend>
            <label className="flex gap-2 mt-2">
              <input type="radio" name="type" checked={!query.type} onChange={() => set('type', '')} /> All
            </label>
            <label className="flex gap-2">
              <input type="radio" name="type" checked={query.type === 'TRADITIONAL'} onChange={() => set('type', 'TRADITIONAL')} />
              Traditional
            </label>
            <label className="flex gap-2">
              <input type="radio" name="type" checked={query.type === 'MODERN'} onChange={() => set('type', 'MODERN')} />
              Modern
            </label>
          </fieldset>
          <label className="block">
            Category
            <select className="mt-1 w-full border border-sand px-3 py-2 bg-paper" value={query.categoryId || ''} onChange={(e) => set('categoryId', e.target.value)}>
              <option value="">All</option>
              {categories.data?.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </label>
          <label className="block">
            Artisan
            <select className="mt-1 w-full border border-sand px-3 py-2 bg-paper" value={query.artisanId || ''} onChange={(e) => set('artisanId', e.target.value)}>
              <option value="">All</option>
              {artisans.data?.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.name}
                </option>
              ))}
            </select>
          </label>
          <label className="block">
            Technique
            <input className="mt-1 w-full border border-sand px-3 py-2 bg-paper" defaultValue={query.technique} onBlur={(e) => set('technique', e.target.value)} />
          </label>
          <label className="block">
            Material
            <input className="mt-1 w-full border border-sand px-3 py-2 bg-paper" defaultValue={query.material} onBlur={(e) => set('material', e.target.value)} />
          </label>
          <div className="flex gap-2">
            <label className="flex-1">
              Min ₹
              <input type="number" className="mt-1 w-full border border-sand px-3 py-2 bg-paper" defaultValue={query.minPrice} onBlur={(e) => set('minPrice', e.target.value)} />
            </label>
            <label className="flex-1">
              Max ₹
              <input type="number" className="mt-1 w-full border border-sand px-3 py-2 bg-paper" defaultValue={query.maxPrice} onBlur={(e) => set('maxPrice', e.target.value)} />
            </label>
          </div>
          <label className="block">
            Sort
            <select className="mt-1 w-full border border-sand px-3 py-2 bg-paper" value={query.sort || 'newest'} onChange={(e) => set('sort', e.target.value)}>
              <option value="newest">Newest</option>
              <option value="price-asc">Price: low to high</option>
              <option value="price-desc">Price: high to low</option>
              <option value="name">Name</option>
            </select>
          </label>
        </form>
        <div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {content.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))}
          </div>
          {!products.isLoading && content.length === 0 && <p className="text-sage">No pieces match these filters.</p>}
          {products.data && products.data.totalPages > 1 && (
            <div className="mt-8 flex gap-2">
              {Array.from({ length: products.data.totalPages }, (_, i) => (
                <button
                  key={i}
                  type="button"
                  className={`px-3 py-1 border ${String(query.page || '0') === String(i) ? 'bg-ink text-cream' : ''}`}
                  onClick={() => set('page', String(i))}
                >
                  {i + 1}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

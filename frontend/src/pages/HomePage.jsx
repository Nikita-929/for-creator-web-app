import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import api from '../api/client'
import ProductCard from '../components/ProductCard'
import Seo from '../components/Seo'

export default function HomePage() {
  const featured = useQuery({ queryKey: ['featured'], queryFn: () => api.get('/api/products/featured').then((r) => r.data) })
  const categories = useQuery({ queryKey: ['categories'], queryFn: () => api.get('/api/categories').then((r) => r.data) })

  const traditional = categories.data?.filter((c) => c.type === 'TRADITIONAL') ?? []
  const modern = categories.data?.filter((c) => c.type === 'MODERN') ?? []

  return (
    <>
      <Seo title="Handmade arts & crafts" description="Shop traditional and contemporary handmade work from Indian studios." />
      <section className="relative min-h-[70vh] flex items-end bg-sage text-cream">
        <img
          src="https://www.buildofy.com/blog/content/images/size/w1000/2024/04/Promo_Ramayana-House.00_00_54_00.Still013.png"
          alt="Hands at a loom with coloured threads"
          className="absolute inset-0 h-full w-full object-cover opacity-50"
        />
        <div className="relative mx-auto max-w-6xl px-4 py-16">
          <p className="uppercase tracking-[0.3em] text-xs">Traditional × modern</p>
          <h1 className="font-display text-5xl md:text-7xl max-w-3xl mt-3">Made by hand. Meant to be used.</h1>
          <p className="mt-4 max-w-xl text-sand">
            For Creators is a storefront for heritage techniques and contemporary studio design — bringing together sarees, scarves, ceramics, metalwork, prints, and more all sold directly with live checkout.
          </p>
          <div className="mt-8 flex gap-4">
            <Link to="/shop" className="bg-clay text-cream px-5 py-3 rounded">
              Shop the collection
            </Link>
            <Link to="/artisans" className="border border-cream px-5 py-3 rounded">
              Meet artisans
            </Link>
          </div>
        </div>
      </section>
      <section className="mx-auto max-w-6xl px-4 py-16 grid md:grid-cols-2 gap-10">
        <div>
          <h2 className="font-display text-3xl">Traditional</h2>
          <ul className="mt-4 space-y-2">
            {traditional.map((c) => (
              <li key={c.id}>
                <Link className="hover:text-clay" to={`/shop?categoryId=${c.id}&type=TRADITIONAL`}>
                  {c.name}
                </Link>
              </li>
            ))}
          </ul>
        </div>
        <div>
          <h2 className="font-display text-3xl">Modern</h2>
          <ul className="mt-4 space-y-2">
            {modern.map((c) => (
              <li key={c.id}>
                <Link className="hover:text-clay" to={`/shop?categoryId=${c.id}&type=MODERN`}>
                  {c.name}
                </Link>
              </li>
            ))}
          </ul>
        </div>
      </section>
      <section className="mx-auto max-w-6xl px-4 pb-16">
        <h2 className="font-display text-3xl mb-8">Featured pieces</h2>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
          {featured.data?.map((p) => (
            <ProductCard key={p.id} product={p} />
          ))}
        </div>
      </section>
    </>
  )
}

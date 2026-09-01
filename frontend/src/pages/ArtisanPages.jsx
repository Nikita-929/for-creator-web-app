import { useQuery } from '@tanstack/react-query'
import { Link, useParams } from 'react-router-dom'
import api from '../api/client'
import ProductCard from '../components/ProductCard'
import Seo from '../components/Seo'

export function ArtisansPage() {
  const { data } = useQuery({ queryKey: ['artisans'], queryFn: () => api.get('/api/artisans').then((r) => r.data) })
  return (
    <div className="mx-auto max-w-6xl px-4 py-10">
      <Seo title="Artisans" description="Meet the makers behind For Creators." />
      <h1 className="font-display text-4xl">Artisans</h1>
      <div className="grid md:grid-cols-3 gap-8 mt-8">
        {data?.map((a) => (
          <Link key={a.id} to={`/artisans/${a.id}`} className="block">
            <img src={a.photo} alt={a.name} className="aspect-[3/4] object-cover w-full bg-sand" />
            <h2 className="font-display text-2xl mt-3">{a.name}</h2>
            <p className="text-sm text-sage">{a.workshopLocation}</p>
          </Link>
        ))}
      </div>
    </div>
  )
}

export function ArtisanDetailPage() {
  const { id } = useParams()
  const { data } = useQuery({ queryKey: ['artisan', id], queryFn: () => api.get(`/api/artisans/${id}`).then((r) => r.data) })
  if (!data) return <p className="p-8">Loading…</p>
  return (
    <div className="mx-auto max-w-6xl px-4 py-10">
      <Seo title={data.name} description={data.bio} />
      <div className="grid md:grid-cols-2 gap-10">
        <img src={data.photo} alt={data.name} className="w-full object-cover aspect-[3/4] bg-sand" />
        <div>
          <h1 className="font-display text-4xl">{data.name}</h1>
          <p className="text-sage mt-2">{data.workshopLocation}</p>
          <p className="mt-6 leading-relaxed">{data.bio}</p>
          <h2 className="font-display text-2xl mt-8">Process</h2>
          <p className="mt-2">{data.processDescription}</p>
        </div>
      </div>
      <h2 className="font-display text-2xl mt-12 mb-6">Pieces</h2>
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8">
        {data.products?.map((p) => (
          <ProductCard key={p.id} product={p} />
        ))}
      </div>
    </div>
  )
}

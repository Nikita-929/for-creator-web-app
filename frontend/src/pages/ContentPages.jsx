import { Link, useParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { useState } from 'react'
import api from '../api/client'
import Seo from '../components/Seo'

export function JournalPage() {
  const { data } = useQuery({ queryKey: ['journal'], queryFn: () => api.get('/api/journal').then((r) => r.data) })
  return (
    <div className="mx-auto max-w-4xl px-4 py-10">
      <Seo title="Journal" description="Technique guides and artisan spotlights." />
      <h1 className="font-display text-4xl">Journal</h1>
      <ul className="mt-8 space-y-8">
        {data?.map((post) => (
          <li key={post.id}>
            <Link to={`/journal/${post.slug}`} className="block group">
              {post.coverImage && <img src={post.coverImage} alt="" className="w-full h-56 object-cover bg-sand" />}
              <h2 className="font-display text-2xl mt-3 group-hover:text-clay">{post.title}</h2>
              <p className="text-sage mt-1">{post.excerpt}</p>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  )
}

export function JournalPostPage() {
  const { slug } = useParams()
  const { data, isError } = useQuery({
    queryKey: ['journal', slug],
    queryFn: () => api.get(`/api/journal/${slug}`).then((r) => r.data),
  })
  if (isError) return <p className="p-8">Post not found.</p>
  if (!data) return <p className="p-8">Loading…</p>
  return (
    <article className="mx-auto max-w-3xl px-4 py-12">
      <Seo title={data.title} description={data.excerpt} />
      {data.coverImage && <img src={data.coverImage} alt="" className="w-full h-72 object-cover mb-8" />}
      <h1 className="font-display text-4xl">{data.title}</h1>
      <p className="mt-6 leading-relaxed whitespace-pre-wrap">{data.body}</p>
    </article>
  )
}

function FormNote({ children }) {
  return <p className="text-sage text-sm mt-3">{children}</p>
}

export function ContactPage() {
  const [form, setForm] = useState({ name: '', email: '', message: '' })
  const [note, setNote] = useState('')

  async function submit(e) {
    e.preventDefault()
    const { data } = await api.post('/api/contact', form)
    setNote(data.message)
    setForm({ name: '', email: '', message: '' })
  }

  return (
    <div className="mx-auto max-w-xl px-4 py-12">
      <Seo title="Contact" />
      <h1 className="font-display text-4xl">Contact</h1>
      <form className="mt-8 space-y-4" onSubmit={submit}>
        <input required placeholder="Name" className="w-full border border-sand px-3 py-2" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input required type="email" placeholder="Email" className="w-full border border-sand px-3 py-2" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <textarea required rows="5" placeholder="Message" className="w-full border border-sand px-3 py-2" value={form.message} onChange={(e) => setForm({ ...form, message: e.target.value })} />
        <button className="bg-ink text-cream px-5 py-3 rounded" type="submit">
          Send
        </button>
      </form>
      {note && <FormNote>{note}</FormNote>}
    </div>
  )
}

export function CustomOrdersPage() {
  const [form, setForm] = useState({ name: '', contact: '', description: '', budgetRange: '' })
  const [note, setNote] = useState('')

  async function submit(e) {
    e.preventDefault()
    await api.post('/api/custom-orders', form)
    setNote('Request received. We will email the studio inbox when SMTP is configured.')
    setForm({ name: '', contact: '', description: '', budgetRange: '' })
  }

  return (
    <div className="mx-auto max-w-xl px-4 py-12">
      <Seo title="Custom orders" description="Commission or bulk enquiry for handmade work." />
      <h1 className="font-display text-4xl">Commissions & bulk</h1>
      <p className="mt-3 text-sage">Describe the piece, timeline, and budget. Rate-limited to keep the inbox usable.</p>
      <form className="mt-8 space-y-4" onSubmit={submit}>
        <input required placeholder="Name" className="w-full border border-sand px-3 py-2" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input required placeholder="Email or phone" className="w-full border border-sand px-3 py-2" value={form.contact} onChange={(e) => setForm({ ...form, contact: e.target.value })} />
        <input placeholder="Budget range (INR)" className="w-full border border-sand px-3 py-2" value={form.budgetRange} onChange={(e) => setForm({ ...form, budgetRange: e.target.value })} />
        <textarea required rows="5" placeholder="What should we make?" className="w-full border border-sand px-3 py-2" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
        <button className="bg-clay text-cream px-5 py-3 rounded" type="submit">
          Submit request
        </button>
      </form>
      {note && <FormNote>{note}</FormNote>}
    </div>
  )
}

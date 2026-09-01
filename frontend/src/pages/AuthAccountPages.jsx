import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import api from '../api/client'
import { useAuth } from '../context/AuthContext'
import ProductCard from '../components/ProductCard'
import Seo from '../components/Seo'
import { inr } from '../lib/format'

export function LoginPage() {
  const { login, user } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('buyer@forcreators.in')
  const [password, setPassword] = useState('Buyer@12345')
  const [error, setError] = useState('')

  if (user) return <Navigate to="/account" replace />

  async function submit(e) {
    e.preventDefault()
    try {
      const next = await login({ email, password })
      navigate(next.role === 'ADMIN' ? '/admin' : '/account')
    } catch (err) {
      setError(err.response?.data?.message || 'Could not sign in')
    }
  }

  return (
    <div className="mx-auto max-w-md px-4 py-12">
      <Seo title="Sign in" />
      <h1 className="font-display text-4xl">Sign in</h1>
      <form className="mt-8 space-y-4" onSubmit={submit}>
        <label className="block text-sm">
          Email
          <input type="email" required className="mt-1 w-full border border-sand px-3 py-2" value={email} onChange={(e) => setEmail(e.target.value)} />
        </label>
        <label className="block text-sm">
          Password
          <input type="password" required className="mt-1 w-full border border-sand px-3 py-2" value={password} onChange={(e) => setPassword(e.target.value)} />
        </label>
        {error && <p className="text-clay text-sm">{error}</p>}
        <button className="bg-ink text-cream px-5 py-3 rounded w-full" type="submit">
          Continue
        </button>
      </form>
      <p className="mt-4 text-sm">
        New here?{' '}
        <Link className="underline" to="/register">
          Create an account
        </Link>
      </p>
      <p className="mt-2 text-xs text-sage">Seeded buyer: buyer@forcreators.in / Buyer@12345 — admin: admin@forcreators.in / Admin@12345</p>
    </div>
  )
}

export function RegisterPage() {
  const { register, user } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [error, setError] = useState('')
  if (user) return <Navigate to="/account" replace />

  async function submit(e) {
    e.preventDefault()
    try {
      await register(form)
      navigate('/account')
    } catch (err) {
      setError(err.response?.data?.message || 'Could not register')
    }
  }

  return (
    <div className="mx-auto max-w-md px-4 py-12">
      <Seo title="Register" />
      <h1 className="font-display text-4xl">Create an account</h1>
      <form className="mt-8 space-y-4" onSubmit={submit}>
        <label className="block text-sm">
          Name
          <input required className="mt-1 w-full border border-sand px-3 py-2" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        </label>
        <label className="block text-sm">
          Email
          <input type="email" required className="mt-1 w-full border border-sand px-3 py-2" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        </label>
        <label className="block text-sm">
          Password
          <input type="password" required minLength={8} className="mt-1 w-full border border-sand px-3 py-2" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        </label>
        {error && <p className="text-clay text-sm">{error}</p>}
        <button className="bg-ink text-cream px-5 py-3 rounded w-full" type="submit">
          Register
        </button>
      </form>
    </div>
  )
}

export function AccountPage() {
  const { user, ready } = useAuth()
  const location = useLocation()
  const wishlist = useQuery({
    queryKey: ['wishlist'],
    enabled: !!user,
    queryFn: () => api.get('/api/wishlist').then((r) => r.data),
  })
  const orders = useQuery({
    queryKey: ['orders'],
    enabled: !!user,
    queryFn: () => api.get('/api/orders').then((r) => r.data),
  })

  if (!ready) return <p className="p-8">Loading…</p>
  if (!user) return <Navigate to="/login" replace />

  return (
    <div className="mx-auto max-w-6xl px-4 py-10">
      <Seo title="Account" />
      <h1 className="font-display text-4xl">Hello, {user.name}</h1>
      {location.state?.paid && <p className="mt-2 text-sage">Payment received. Thank you.</p>}
      <h2 className="font-display text-2xl mt-10">Wishlist</h2>
      <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-8 mt-4">
        {wishlist.data?.map((p) => (
          <ProductCard key={p.id} product={p} />
        ))}
      </div>
      {!wishlist.data?.length && <p className="text-sm text-sage mt-2">Save pieces while signed in — they persist on your account.</p>}
      <h2 className="font-display text-2xl mt-12">Orders</h2>
      <ul className="mt-4 space-y-4">
        {orders.data?.map((o) => (
          <li key={o.id} className="border border-sand p-4">
            <p>
              #{o.id} — {o.status} / {o.paymentStatus} — {inr(o.total)}
            </p>
            <p className="text-sm text-sage">{o.items?.map((i) => `${i.productName} × ${i.quantity}`).join(', ')}</p>
          </li>
        ))}
      </ul>
    </div>
  )
}

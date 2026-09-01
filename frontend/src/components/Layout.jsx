import { Link, NavLink, Outlet } from 'react-router-dom'
import { useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import api from '../api/client'

const links = [
  { to: '/shop', label: 'Shop' },
  { to: '/artisans', label: 'Artisans' },
  { to: '/journal', label: 'Journal' },
  { to: '/about', label: 'About' },
  { to: '/custom-orders', label: 'Commissions' },
]

export default function Layout() {
  const { user, logout } = useAuth()
  const { count } = useCart()
  const [open, setOpen] = useState(false)
  const [email, setEmail] = useState('')
  const [note, setNote] = useState('')

  async function subscribe(e) {
    e.preventDefault()
    try {
      const { data } = await api.post('/api/newsletter', { email })
      setNote(data.message)
      setEmail('')
    } catch {
      setNote('Could not subscribe right now.')
    }
  }

  return (
    <div className="min-h-svh flex flex-col">
      <a href="#main" className="sr-only focus:not-sr-only focus:absolute focus:p-2 bg-ink text-cream">
        Skip to content
      </a>
      <header className="border-b border-sand bg-paper/90 sticky top-0 z-20 backdrop-blur">
        <div className="mx-auto max-w-6xl px-4 py-3 flex items-center justify-between gap-4">
          <Link to="/" className="font-display text-2xl text-ink">
            For Creators
          </Link>
          <nav className="hidden md:flex gap-6 text-sm" aria-label="Primary">
            {links.map((l) => (
              <NavLink key={l.to} to={l.to} className={({ isActive }) => (isActive ? 'text-clay' : 'hover:text-clay')}>
                {l.label}
              </NavLink>
            ))}
          </nav>
          <div className="flex items-center gap-3 text-sm">
            <Link to="/cart" className="hover:text-clay">
              Cart ({count})
            </Link>
            {user ? (
              <>
                <Link to="/account" className="hover:text-clay">
                  {user.name}
                </Link>
                {user.role === 'ADMIN' && (
                  <Link to="/admin" className="text-sage font-medium">
                    Admin
                  </Link>
                )}
                <button type="button" onClick={logout} className="hover:text-clay">
                  Log out
                </button>
              </>
            ) : (
              <Link to="/login" className="hover:text-clay">
                Sign in
              </Link>
            )}
            <button type="button" className="md:hidden" aria-label="Menu" onClick={() => setOpen((v) => !v)}>
              Menu
            </button>
          </div>
        </div>
        {open && (
          <nav className="md:hidden px-4 pb-4 flex flex-col gap-2" aria-label="Mobile">
            {links.map((l) => (
              <NavLink key={l.to} to={l.to} onClick={() => setOpen(false)}>
                {l.label}
              </NavLink>
            ))}
          </nav>
        )}
      </header>
      <main id="main" className="flex-1">
        <Outlet />
      </main>
      <footer className="border-t border-sand bg-ink text-cream mt-16">
        <div className="mx-auto max-w-6xl px-4 py-12 grid md:grid-cols-3 gap-8">
          <div>
            <p className="font-display text-2xl">For Creators</p>
            <p className="mt-3 text-sm text-sand max-w-sm">
              A storefront for handmade work — heritage techniques and contemporary studio pieces, sold directly from
              the workshop.
            </p>
          </div>
          <div className="text-sm flex flex-col gap-2">
            <Link to="/contact">Contact</Link>
            <Link to="/custom-orders">Custom orders</Link>
            <Link to="/shop?type=TRADITIONAL">Traditional</Link>
            <Link to="/shop?type=MODERN">Modern</Link>
          </div>
          <form onSubmit={subscribe} className="text-sm">
            <label htmlFor="nl" className="block mb-2">
              Newsletter
            </label>
            <div className="flex gap-2">
              <input
                id="nl"
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="flex-1 px-3 py-2 rounded bg-paper text-ink"
                placeholder="you@email.com"
              />
              <button type="submit" className="bg-clay px-4 py-2 rounded">
                Join
              </button>
            </div>
            {note && <p className="mt-2 text-sand">{note}</p>}
            <p className="mt-2 text-xs text-sand/80">Stored locally for this demo.</p>
          </form>
        </div>
      </footer>
    </div>
  )
}

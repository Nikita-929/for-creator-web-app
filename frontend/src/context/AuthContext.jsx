import { createContext, useContext, useEffect, useMemo, useState } from 'react'
import api from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [ready, setReady] = useState(false)

  useEffect(() => {
    const token = localStorage.getItem('fc_token')
    if (!token) {
      setReady(true)
      return
    }
    api
      .get('/api/auth/me')
      .then((res) => setUser(res.data))
      .catch(() => localStorage.removeItem('fc_token'))
      .finally(() => setReady(true))
  }, [])

  const value = useMemo(
    () => ({
      user,
      ready,
      async login(payload) {
        const { data } = await api.post('/api/auth/login', payload)
        localStorage.setItem('fc_token', data.token)
        setUser(data.user)
        return data.user
      },
      async register(payload) {
        const { data } = await api.post('/api/auth/register', payload)
        localStorage.setItem('fc_token', data.token)
        setUser(data.user)
        return data.user
      },
      logout() {
        localStorage.removeItem('fc_token')
        setUser(null)
      },
    }),
    [user, ready],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}

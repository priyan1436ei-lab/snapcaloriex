-- ==============================================================
-- CalorieLens Supabase PostgreSQL Schema
-- Run this script in your Supabase SQL Editor:
-- Dashboard -> SQL Editor -> New query -> Paste & Run
-- ==============================================================

-- 1. Create Meals table
CREATE TABLE IF NOT EXISTS public.meals (
    id BIGINT PRIMARY KEY,
    user_id TEXT NOT NULL,
    meal_type TEXT NOT NULL,
    date_string TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    total_calories INT NOT NULL DEFAULT 0,
    total_protein NUMERIC(6, 2) NOT NULL DEFAULT 0,
    total_carbs NUMERIC(6, 2) NOT NULL DEFAULT 0,
    total_fat NUMERIC(6, 2) NOT NULL DEFAULT 0,
    total_fiber NUMERIC(6, 2) NOT NULL DEFAULT 0,
    notes TEXT,
    items_json JSONB NOT NULL DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. Create User Goals table
CREATE TABLE IF NOT EXISTS public.user_goals (
    user_id TEXT PRIMARY KEY,
    daily_calories INT NOT NULL DEFAULT 2000,
    protein_grams INT NOT NULL DEFAULT 140,
    carbs_grams INT NOT NULL DEFAULT 220,
    fat_grams INT NOT NULL DEFAULT 65,
    water_goal_ml INT NOT NULL DEFAULT 2500,
    current_water_ml INT NOT NULL DEFAULT 0,
    target_weight_kg NUMERIC(5, 2) NOT NULL DEFAULT 70.0,
    goalType TEXT NOT NULL DEFAULT 'Maintenance',
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 3. Enable Row Level Security (RLS)
ALTER TABLE public.meals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_goals ENABLE ROW LEVEL SECURITY;

-- 4. Create public access policies for API Key access
-- (Allows reading and inserting based on apikey / anon key)
DROP POLICY IF EXISTS "Allow anon read meals" ON public.meals;
CREATE POLICY "Allow anon read meals" ON public.meals
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Allow anon insert/upsert meals" ON public.meals;
CREATE POLICY "Allow anon insert/upsert meals" ON public.meals
    FOR ALL USING (true) WITH CHECK (true);

DROP POLICY IF EXISTS "Allow anon read goals" ON public.user_goals;
CREATE POLICY "Allow anon read goals" ON public.user_goals
    FOR SELECT USING (true);

DROP POLICY IF EXISTS "Allow anon insert/upsert goals" ON public.user_goals;
CREATE POLICY "Allow anon insert/upsert goals" ON public.user_goals
    FOR ALL USING (true) WITH CHECK (true);

-- 5. Create index for fast date and user queries
CREATE INDEX IF NOT EXISTS idx_meals_user_date ON public.meals (user_id, date_string);
